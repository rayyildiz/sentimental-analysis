package com.rayyildiz.sentiment_analyzer

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{Sink, Source}
import com.rayyildiz.sentiment_analyzer.models._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class BootApplicationSpec extends FlatSpec with ScalaFutures with BeforeAndAfterAll with Matchers with JsonSerialization {
  BootApplication.main(Array())

  implicit val system = BootApplication.system

  import system.dispatcher

  implicit val materializer = BootApplication.materializer
  // scalastyle:off magic.number
  implicit val defaultPatience = PatienceConfig(timeout = Span(10, Seconds), interval = Span(1000, Millis))
  // scalastyle:on magic.number

  override def afterAll(): Unit = system.terminate()

  private def sendRequest(request: HttpRequest) =
    Source
      .single(request)
      .via(
        Http().outgoingConnection(host = BootApplication.hostAddress, port = BootApplication.port)
      )
      .runWith(Sink.head)

  "The Boot Application" should "return Pong on POST /ping" in {
    val ping           = Ping("Hello")
    val requestEntity  = Await.result(Marshal(ping).to[RequestEntity], Duration.Inf)
    val responseFuture = sendRequest(HttpRequest(uri = "/ping", method = HttpMethods.POST, entity = requestEntity))
    whenReady(responseFuture) { response =>
      val pongFuture = Unmarshal(response.entity).to[Pong]
      whenReady(pongFuture) { pong =>
        pong.message shouldBe ping.message
      }
    }
  }

  it should "return 404 on GET /NotDefinedRoute" in {
    val responseFuture = sendRequest(HttpRequest(uri = "/NotDefinedRoute", method = HttpMethods.GET))
    whenReady(responseFuture) { response =>
      response.status shouldBe StatusCodes.OK

      val errorMessageFuture = Unmarshal(response.entity).to[ErrorMessage]
      whenReady(errorMessageFuture) { errorMessage =>
        errorMessage.statusCode shouldBe StatusCodes.NotFound.intValue
      }
    }
  }

  it should "return UP on GET /health" in {
    val responseFuture = sendRequest(HttpRequest(uri = "/health", method = HttpMethods.GET))
    whenReady(responseFuture) { response =>
      val applicationStatusResponseFuture = Unmarshal(response.entity).to[ApplicationStatus]
      whenReady(applicationStatusResponseFuture) { applicationStatus =>
        applicationStatus.status shouldBe true
      }
    }
  }

  it should "return information on GET /info" in {
    val responseFuture = sendRequest(HttpRequest(uri = "/info", method = HttpMethods.GET))
    whenReady(responseFuture) { response =>
      val applicationInfoResponseFuture = Unmarshal(response.entity).to[ApplicationInformation]
      whenReady(applicationInfoResponseFuture) { applicationInfo =>
        applicationInfo.version shouldBe "1.0"
      }
    }
  }

  it should "clean text properly on POST /api/clean" in {
    val cleanReq       = CleanTextRequest("<p><a href='http://example.com/' onclick='stealCookies()'>Link</a></p>")
    val requestEntity  = Await.result(Marshal(cleanReq).to[RequestEntity], Duration.Inf)
    val responseFuture = sendRequest(HttpRequest(uri = "/api/clean", method = HttpMethods.POST, entity = requestEntity))

    whenReady(responseFuture) { response =>
      val cleanedTextResponseFuture = Unmarshal(response.entity).to[CleanTextResponse]
      whenReady(cleanedTextResponseFuture) { cleanedTextResponse =>
        cleanedTextResponse.text shouldBe "Link"
      }
    }
  }

  it should "language detection on POST /api/detect for English Text" in {
    val cleanReq = DetectionRequest(
      """The company was founded in 2010, through the coming together of two different branches of science, in the pursuit of a deeper understanding of consumer behaviour.
        |We continue to merge different disciplines, cultures and skill sets, with about 100 people from over 20 countries adding their magic to the mix.
        |Overlaying this with exceptional technological and analytical skills, our capability, diversity and breadth of expertise makes us truly unique in the industry. """.stripMargin
    )
    val requestEntity = Await.result(Marshal(cleanReq).to[RequestEntity], Duration.Inf)
    val responseFuture =
      sendRequest(HttpRequest(uri = "/api/detect", method = HttpMethods.POST, entity = requestEntity))

    whenReady(responseFuture) { response =>
      val detectResponseFuture = Unmarshal(response.entity).to[DetectionResponse]
      whenReady(detectResponseFuture) { detectResponse =>
        detectResponse.language shouldBe "en"
        detectResponse.confidence should be > 0.989f
      }
    }
  }

  it should "language detection on POST /api/detect for Turkish Text" in {
    val cleanReq = DetectionRequest(
      "Google 13 Mart tarihinde bazı uyuglamarını kapatacağını duyurudu. Bu uygulamalardan birisi olan google reader kapatılması ise birçok kişi gibi ben de garip karşıladım. Bu sıralarda ise feedly yeni keşfetmiştim. Neyseki feedly bir çalışma yapaçağını duyurdu. Benim gibi günde 500 den fazla blog’u takip eden birisi için güzel bir haber bu."
    )
    val requestEntity = Await.result(Marshal(cleanReq).to[RequestEntity], Duration.Inf)
    val responseFuture =
      sendRequest(HttpRequest(uri = "/api/detect", method = HttpMethods.POST, entity = requestEntity))

    whenReady(responseFuture) { response =>
      val detectResponseFuture = Unmarshal(response.entity).to[DetectionResponse]
      whenReady(detectResponseFuture) { detectResponse =>
        detectResponse.language shouldBe "tr"
        detectResponse.confidence should be > 0.989f
      }
    }
  }

  it should "extract words on POST /api/extract" in {
    val extractReq    = ExtractRequest("President Obama is speaking at the White House.")
    val requestEntity = Await.result(Marshal(extractReq).to[RequestEntity], Duration.Inf)
    val responseFuture =
      sendRequest(HttpRequest(uri = "/api/extract", method = HttpMethods.POST, entity = requestEntity))

    whenReady(responseFuture) { response =>
      val extractResponseFuture = Unmarshal(response.entity).to[ExtractResponse]
      whenReady(extractResponseFuture) { extractResponse =>
        extractResponse.entities.size should be >= 2 // Obama , White House
      }
    }
  }

  it should "sentiment analysis for a text on POST /api/sentiment" in {
    val request = SentimentRequest(
      """Cristiano Ronaldo not only lifted his first international trophy, but also his country’s first ever international trophy when his team won the 2016 Euro Championship.
        |Messi, on the other hand, lost his fourth international final when Argentina lost to Chile in the 2016 Copa America Centenario, at the end of which the Barcelona man announced his retirement from international football.""".stripMargin
    )
    val requestEntity = Await.result(Marshal(request).to[RequestEntity], Duration.Inf)
    val responseFuture =
      sendRequest(HttpRequest(uri = "/api/sentiment", method = HttpMethods.POST, entity = requestEntity))

    whenReady(responseFuture) { response =>
      val sentimentResponseFuture = Unmarshal(response.entity).to[SentimentResponse]
      whenReady(sentimentResponseFuture) { sentimentResponse =>
        sentimentResponse.entities.size shouldBe 2 // To sentences about CR ve And Messi
        sentimentResponse.score should be <= 0.0f
        sentimentResponse.documentFeeling shouldBe "NEGATIVE" // This is a critical entry, so it is negative feelings
      }
    }
  }

  it should "determination relationship between feature and sentiment on POST /api/determination" in {
    val request =
      DeterminationRequest("Ask not what your country can do for you, ask what you can do for your country.")
    val requestEntity = Await.result(Marshal(request).to[RequestEntity], Duration.Inf)
    val responseFuture =
      sendRequest(HttpRequest(uri = "/api/determination", method = HttpMethods.POST, entity = requestEntity))

    whenReady(responseFuture) { response =>
      val determinationResponseFuture = Unmarshal(response.entity).to[DeterminationResponse]
      whenReady(determinationResponseFuture) { determinationResponse =>
        determinationResponse.sentences.size shouldBe 1
        determinationResponse.tokens.size should be > 5
      }
    }
  }

  it should "All analysis together on POST /api/analysis" in {
    val request       = AnalysisRequest("Ask not what your country can do for you, ask what you can do for your country.")
    val requestEntity = Await.result(Marshal(request).to[RequestEntity], Duration.Inf)
    val responseFuture =
      sendRequest(HttpRequest(uri = "/api/analysis", method = HttpMethods.POST, entity = requestEntity))

    whenReady(responseFuture) { response =>
      val analysisResponseFuture = Unmarshal(response.entity).to[AnalysisResponse]
      whenReady(analysisResponseFuture) { analysisResponse =>
        analysisResponse.clean.text.size should be > 77
        analysisResponse.detect.language shouldBe "en"
        analysisResponse.extract.entities.size shouldBe 2
        analysisResponse.sentiment.documentFeeling shouldBe "NEGATIVE"
        analysisResponse.determination.sentences.size shouldBe 1
      }
    }
  }
}
