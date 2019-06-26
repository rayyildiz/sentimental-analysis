package com.rayyildiz.sentiment_analyzer.actors
import akka.actor.{Actor, ActorRef}
import com.google.cloud.language.v1.Document.Type
import com.google.cloud.language.v1.{Document, LanguageServiceClient}
import com.rayyildiz.sentiment_analyzer.models.{SentimentedSentences, SentimentedWords}

import scala.collection.JavaConverters._

class SentimentActor(client: LanguageServiceClient, reply: ActorRef) extends Actor {

  override def receive: Receive = {
    case text: String =>
      val doc      = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build()
      val response = client.analyzeSentiment(doc)

      val documentSentiment = response.getDocumentSentiment

      val list = response.getSentencesList.asScala.map { str =>
        val sentiment           = str.getSentiment
        val feelingText: String = if (sentiment.getScore > 0) "POSITIVE" else "NEGATIVE"

        SentimentedSentences(
          sentence = str.getText.getContent,
          score = sentiment.getScore,
          magnitude = sentiment.getMagnitude,
          feeling = feelingText
        )
      }.toList

      val feelingText: String = if (documentSentiment.getScore > 0) "POSITIVE" else "NEGATIVE"

      reply ! SentimentedWords(
        magnitude = documentSentiment.getMagnitude,
        score = documentSentiment.getScore,
        documentFeeling = feelingText,
        entities = list
      )
    case _ => sender() ! "Not implemented for other types"
  }

}
