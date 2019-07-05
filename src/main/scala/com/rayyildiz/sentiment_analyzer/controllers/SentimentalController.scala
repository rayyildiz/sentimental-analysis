package com.rayyildiz.sentiment_analyzer.controllers

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import com.google.cloud.language.v1.LanguageServiceClient
import com.google.cloud.translate.{Translate, TranslateOptions}
import com.rayyildiz.sentiment_analyzer.actors._
import com.rayyildiz.sentiment_analyzer.models.ReqResponseModels._
import com.rayyildiz.sentiment_analyzer.models.TextModels._
import javax.inject.{Inject, Singleton}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SentimentalController @Inject()(
    private val system: ActorSystem
)(implicit val executionContext: ExecutionContext) {
  lazy val languageApi: LanguageServiceClient = LanguageServiceClient.create()
  lazy val translateApi: Translate            = TranslateOptions.getDefaultInstance.getService

  implicit val timeout: Timeout = Timeout(20 seconds)

  def analysis(text: String): Future[AnalysisResponse] =
    for {
      c   <- clean(text)
      d   <- detect(text)
      e   <- extract(text)
      s   <- sentiment(text)
      det <- determination(text)
    } yield AnalysisResponse(c, d, e, s, det)

  def clean(text: String): Future[CleanTextResponse] = {
    implicit val timeout: Timeout = Timeout(2 seconds)
    val nlp: ActorRef             = system.actorOf(NLPActor(languageApi, translateApi))

    (nlp ? CleanText(text)).mapTo[CleanTextActor.CleanedText].map(cleanedText => CleanTextResponse(cleanedText.text))
  }

  def detect(text: String): Future[DetectionResponse] = {
    implicit val timeout: Timeout = Timeout(3 seconds)

    val nlp: ActorRef = system.actorOf(NLPActor(languageApi, translateApi))
    (nlp ? LanguageDetectText(text))
      .mapTo[LanguageDetectionActor.LanguageDetectedText]
      .map(langDetect => DetectionResponse(langDetect.language, langDetect.confidence))
  }

  def extract(text: String): Future[ExtractResponse] = {
    implicit val timeout: Timeout = Timeout(5 seconds)
    val nlp: ActorRef             = system.actorOf(NLPActor(languageApi, translateApi))

    (nlp ? ExtractWord(text))
      .mapTo[ExtractorActor.ExtractedWords]
      .map(word => ExtractResponse(word.entities))
  }

  def sentiment(text: String): Future[SentimentResponse] = {
    implicit val timeout: Timeout = Timeout(5 seconds)

    val nlp: ActorRef = system.actorOf(NLPActor(languageApi, translateApi))

    (nlp ? SentimentWord(text)).mapTo[SentimentActor.SentimentedWords].map { sentiment =>
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
      .mapTo[RelationActor.DeterminationRelationResult]
      .map(det => DeterminationResponse(det.sentences, det.tokens))
  }
}
