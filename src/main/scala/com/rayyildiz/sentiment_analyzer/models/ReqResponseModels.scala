package com.rayyildiz.sentiment_analyzer.models

import com.rayyildiz.sentiment_analyzer.models.TextModels.DeterminationToken

object ReqResponseModels {

  import com.rayyildiz.sentiment_analyzer.actors.ExtractorActor.ExtractedEntity
  import com.rayyildiz.sentiment_analyzer.actors.RelationActor.DeterminationSentence
  import com.rayyildiz.sentiment_analyzer.actors.SentimentActor.SentimentedSentences
  case class CleanTextRequest(text: String)

  case class CleanTextResponse(text: String)

  case class DetectionRequest(text: String)

  case class DetectionResponse(language: String, confidence: Float)

  case class ExtractRequest(text: String)

  case class ExtractResponse(entities: List[ExtractedEntity])

  case class SentimentRequest(text: String)

  case class SentimentResponse(
      magnitude: Float,
      score: Float,
      documentFeeling: String,
      entities: List[SentimentedSentences]
  )

  case class DeterminationRequest(text: String)

  case class DeterminationResponse(sentences: List[DeterminationSentence], tokens: List[DeterminationToken])

  case class AnalysisRequest(text: String)

  case class AnalysisResponse(
      clean: CleanTextResponse,
      detect: DetectionResponse,
      extract: ExtractResponse,
      sentiment: SentimentResponse,
      determination: DeterminationResponse
  )
}
