package com.rayyildiz.sentiment_analyzer.models

object TextModels {

  // Clean text
  case class CleanText(text: String)

  // Extract Important Words
  case class ExtractWord(text: String)

  // Extract Words Indicating Sentiment
  case class SentimentWord(text: String)

  // Language detect text
  case class LanguageDetectText(text: String)

  case class DeterminationRelationWord(text: String)

  case class DeterminationToken(
      lemma: String,
      tag: String,
      number: Option[String],
      person: Option[String],
      mood: Option[String],
      tense: Option[String]
  )

}
