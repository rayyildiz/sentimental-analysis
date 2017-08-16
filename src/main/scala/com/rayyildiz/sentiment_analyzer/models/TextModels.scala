package com.rayyildiz.sentiment_analyzer.models

// Clean Text
case class CleanedText(text: String)

// Language Check
case class LanguageDetection(language: String, confidence: Float)

// Extract Important Words
case class ExtractWord(text: String)

case class ExtractedEntity(word: String, entityType: String, salience: Float)

case class ExtractedWords(entities: List[ExtractedEntity])

// Extract Words Indicating Sentiment
case class SentimentWord(text: String)

case class SentimentedSentences(sentence: String, feeling: String, score: Float, magnitude: Float)

case class SentimentedWords(
  magnitude: Float,
  score: Float,
  documentFeeling: String,
  entities: List[SentimentedSentences]
)

case class DeterminationRelationWord(text: String)

case class DeterminationSentence(content: String, beginOffset: Int)

case class DeterminationToken(
  lemma: String,
  tag: String,
  number: Option[String],
  person: Option[String],
  mood: Option[String],
  tense: Option[String]
)

case class DeterminationRelationResult(sentences: List[DeterminationSentence], tokens: List[DeterminationToken])
