package com.rayyildiz.sentiment_analyzer.routes

import javax.inject.{Inject, Singleton}

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rayyildiz.sentiment_analyzer.controllers.{ApplicationController, SentimentalController}
import com.rayyildiz.sentiment_analyzer.models._

@Singleton
class RestRoutes @Inject()(
    private val applicationController: ApplicationController,
    private val sentimentalController: SentimentalController
) extends JsonSerialization {

  def apply(): Route =
    path("ping") {
      post {
        entity(as[Ping]) { ping =>
          complete(applicationController.ping(ping))
        }
      }
    } ~ pathPrefix("api") {
      path("clean") {
        post {
          entity(as[CleanTextRequest]) { entity =>
            complete(sentimentalController.clean(entity.text))
          }
        }
      } ~ path("detect") {
        post {
          entity(as[DetectionRequest]) { entity =>
            complete(sentimentalController.detect(entity.text))
          }
        }
      } ~ path("extract") {
        post {
          entity(as[ExtractRequest]) { entity =>
            complete(sentimentalController.extract(entity.text))
          }
        }
      } ~ (path("sentiment")) {
        post {
          entity(as[SentimentRequest]) { entity =>
            complete(sentimentalController.sentiment(entity.text))
          }
        }
      } ~ path("determination") {
        post {
          entity(as[DeterminationRequest]) { entity =>
            complete(sentimentalController.determination(entity.text))
          }
        }
      } ~ path("analysis") {
        post {
          entity(as[AnalysisRequest]) { entity =>
            complete(sentimentalController.analysis(entity.text))
          }
        }
      }
    }
}
