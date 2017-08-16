package com.rayyildiz.sentiment_analyzer.models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait JsonSerialization extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val applicationHealthFormat = jsonFormat2(ApplicationStatus)
  implicit val applicationInformationFormat = jsonFormat2(ApplicationInformation)

  implicit val pingFormat = jsonFormat1(Ping)
  implicit val pongFormat = jsonFormat1(Pong)

  implicit val errorMessageFormat = jsonFormat2(ErrorMessage)

  implicit val cleanTextRequestFormat = jsonFormat1(CleanTextRequest)
  implicit val cleanTextResponseFormat = jsonFormat1(CleanTextResponse)

  implicit val detectionRequestFormat = jsonFormat1(DetectionRequest)
  implicit val detectionResponseFormat = jsonFormat2(DetectionResponse)

  implicit val extractedEntityFormat = jsonFormat3(ExtractedEntity)
  implicit val extractRequestFormat = jsonFormat1(ExtractRequest)
  implicit val extractResponseFormat = jsonFormat1(ExtractResponse)

  implicit val sentimentedSentencesFormat = jsonFormat4(SentimentedSentences)
  implicit val sentimentRequestFormat = jsonFormat1(SentimentRequest)
  implicit val sentimentResponseFormat = jsonFormat4(SentimentResponse)

  implicit val determinationSentenceFormat = jsonFormat2(DeterminationSentence)
  implicit val determinationTokenFormat = jsonFormat6(DeterminationToken)
  implicit val determinationRequestFormat = jsonFormat1(DeterminationRequest)
  implicit val determinationResponseFormat = jsonFormat2(DeterminationResponse)

  implicit val analysisRequestFormat = jsonFormat1(AnalysisRequest)
  implicit val analysisResponseFormat = jsonFormat5(AnalysisResponse)
}
