package com.rayyildiz.sentiment_analyzer.controllers

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.google.cloud.language.v1.LanguageServiceClient
import com.google.cloud.translate.{Translate, TranslateOptions}
import com.rayyildiz.sentiment_analyzer.actors.NLPActor
import com.rayyildiz.sentiment_analyzer.models._
import javax.inject.{Inject, Singleton}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SentimentalController @Inject()(
    private val system: ActorSystem
)(implicit val executionContext: ExecutionContext) {
  lazy val languageApi: LanguageServiceClient = LanguageServiceClient.create()
  lazy val translateApi: Translate            = TranslateOptions.getDefaultInstance.getService()

  def analysis(text: String): Future[AnalysisResponse] = {
    implicit val timeout: Timeout = Timeout(20 seconds)
    val response = for {
      c   <- clean(text)
      d   <- detect(text)
      e   <- extract(text)
      s   <- sentiment(text)
      det <- determination(text)
    } yield (c, d, e, s, det)

    response.map { r =>
      AnalysisResponse(r._1, r._2, r._3, r._4, r._5)
    }
  }

  def clean(text: String): Future[CleanTextResponse] = {
    implicit val timeout: Timeout = Timeout(2 seconds)
    val nlp: ActorRef             = system.actorOf(NLPActor(languageApi, translateApi))

    (nlp ? CleanText(text)).mapTo[CleanedText].map(cleanedText => CleanTextResponse(cleanedText.text))
  }

  def detect(text: String): Future[DetectionResponse] = {
    implicit val timeout: Timeout = Timeout(3 seconds)

    val nlp: ActorRef = system.actorOf(NLPActor(languageApi, translateApi))
    (nlp ? LanguageDetectText(text))
      .mapTo[LanguageDetectedText]
      .map(langDetect => DetectionResponse(langDetect.language, langDetect.confidence))
  }

  def extract(text: String): Future[ExtractResponse] = {
    implicit val timeout: Timeout = Timeout(5 seconds)
    val nlp: ActorRef             = system.actorOf(NLPActor(languageApi, translateApi))

    (nlp ? ExtractWord(text))
      .mapTo[ExtractedWords]
      .map(word => ExtractResponse(word.entities))
  }

  def sentiment(text: String): Future[SentimentResponse] = {
    implicit val timeout: Timeout = Timeout(5 seconds)

    val nlp: ActorRef = system.actorOf(NLPActor(languageApi, translateApi))

    (nlp ? SentimentWord(text)).mapTo[SentimentedWords].map { sentiment =>
      SentimentResponse(
        magnitude = sentiment.magnitude,
        score = sentiment.score,
        documentFeeling = sentiment.documentFeeling,
        entities = sentiment.entities
      )
    }
  }

  def determination(text: String): Future[DeterminationResponse] = {
    implicit val timeout: Timeout = Timeout(5 seconds)

    val nlp: ActorRef = system.actorOf(NLPActor(languageApi, translateApi))
    (nlp ? DeterminationRelationWord(text))
      .mapTo[DeterminationRelationResult]
      .map(det => DeterminationResponse(det.sentences, det.tokens))
  }
}
