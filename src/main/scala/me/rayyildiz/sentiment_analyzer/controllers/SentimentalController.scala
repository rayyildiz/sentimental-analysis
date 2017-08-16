package me.rayyildiz.sentiment_analyzer.controllers

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import me.rayyildiz.sentiment_analyzer.actors.{CleanTextActor, LanguageDetectionActor, NLPActor}
import me.rayyildiz.sentiment_analyzer.models.{DeterminationRelationWord, _}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SentimentalController @Inject()(
  private val system: ActorSystem
)(implicit val executionContext: ExecutionContext) {

  def analysis(text: String): Future[AnalysisResponse] = {
    implicit val timeout = Timeout(120 seconds)
    val response = for {
      c <- clean(text)
      d <- detect(text)
      e <- extract(text)
      s <- sentiment(text)
      det <- determination(text)
    } yield (c, d, e, s, det)

    response.map { r =>
      AnalysisResponse(r._1, r._2, r._3, r._4, r._5)
    }
  }

  def clean(text: String): Future[CleanTextResponse] = {
    implicit val timeout = Timeout(5 seconds)
    val cleanTextActorRef: ActorRef = system.actorOf(CleanTextActor.props)
    (cleanTextActorRef ? text).mapTo[CleanedText].map(cleanedText => CleanTextResponse(cleanedText.text))
  }

  def detect(text: String): Future[DetectionResponse] = {
    implicit val timeout = Timeout(20 seconds)

    val detectionActorRef: ActorRef = system.actorOf(LanguageDetectionActor.props)
    (detectionActorRef ? text)
      .mapTo[LanguageDetection]
      .map(langDetect => DetectionResponse(langDetect.language, langDetect.confidence))
  }

  def extract(text: String): Future[ExtractResponse] = {
    implicit val timeout = Timeout(45 seconds)
    val nlpActor: ActorRef = system.actorOf(NLPActor.props)

    (nlpActor ? ExtractWord(text))
      .mapTo[ExtractedWords]
      .map(word => ExtractResponse(word.entities))
  }

  def sentiment(text: String): Future[SentimentResponse] = {
    implicit val timeout = Timeout(60 seconds)
    val nlpActor: ActorRef = system.actorOf(NLPActor.props)

    (nlpActor ? SentimentWord(text)).mapTo[SentimentedWords].map { sentiment =>
      SentimentResponse(
        magnitude = sentiment.magnitude,
        score = sentiment.score,
        documentFeeling = sentiment.documentFeeling,
        entities = sentiment.entities
      )
    }
  }

  def determination(text: String): Future[DeterminationResponse] = {
    implicit val timeout = Timeout(90 seconds)
    val nlpActor: ActorRef = system.actorOf(NLPActor.props)

    (nlpActor ? DeterminationRelationWord(text))
      .mapTo[DeterminationRelationResult]
      .map(det => DeterminationResponse(det.sentences, det.tokens))
  }
}
