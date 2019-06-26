package com.rayyildiz.sentiment_analyzer.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.rayyildiz.sentiment_analyzer.actors.ExtractorActor.ExtractedEntity
import com.rayyildiz.sentiment_analyzer.actors.RelationActor.DeterminationSentence
import com.rayyildiz.sentiment_analyzer.actors.SentimentActor.SentimentedSentences
import spray.json._

trait JsonSerialization extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val applicationHealthFormat: RootJsonFormat[ApplicationStatus]           = jsonFormat2(ApplicationStatus)
  implicit val applicationInformationFormat: RootJsonFormat[ApplicationInformation] = jsonFormat2(ApplicationInformation)

  implicit val pingFormat: RootJsonFormat[Ping] = jsonFormat1(Ping)
  implicit val pongFormat: RootJsonFormat[Pong] = jsonFormat1(Pong)

  implicit val errorMessageFormat: RootJsonFormat[ErrorMessage] = jsonFormat2(ErrorMessage)

  implicit val cleanTextRequestFormat: RootJsonFormat[CleanTextRequest]   = jsonFormat1(CleanTextRequest)
  implicit val cleanTextResponseFormat: RootJsonFormat[CleanTextResponse] = jsonFormat1(CleanTextResponse)

  implicit val detectionRequestFormat: RootJsonFormat[DetectionRequest]   = jsonFormat1(DetectionRequest)
  implicit val detectionResponseFormat: RootJsonFormat[DetectionResponse] = jsonFormat2(DetectionResponse)

  implicit val extractedEntityFormat: RootJsonFormat[ExtractedEntity] = jsonFormat3(ExtractedEntity)
  implicit val extractRequestFormat: RootJsonFormat[ExtractRequest]   = jsonFormat1(ExtractRequest)
  implicit val extractResponseFormat: RootJsonFormat[ExtractResponse] = jsonFormat1(ExtractResponse)

  implicit val sentimentedSentencesFormat: RootJsonFormat[SentimentedSentences] = jsonFormat4(SentimentedSentences)
  implicit val sentimentRequestFormat: RootJsonFormat[SentimentRequest]         = jsonFormat1(SentimentRequest)
  implicit val sentimentResponseFormat: RootJsonFormat[SentimentResponse]       = jsonFormat4(SentimentResponse)

  implicit val determinationSentenceFormat: RootJsonFormat[DeterminationSentence] = jsonFormat2(DeterminationSentence)
  implicit val determinationTokenFormat: RootJsonFormat[DeterminationToken]       = jsonFormat6(DeterminationToken)
  implicit val determinationRequestFormat: RootJsonFormat[DeterminationRequest]   = jsonFormat1(DeterminationRequest)
  implicit val determinationResponseFormat: RootJsonFormat[DeterminationResponse] = jsonFormat2(DeterminationResponse)

  implicit val analysisRequestFormat: RootJsonFormat[AnalysisRequest]   = jsonFormat1(AnalysisRequest)
  implicit val analysisResponseFormat: RootJsonFormat[AnalysisResponse] = jsonFormat5(AnalysisResponse)
}
