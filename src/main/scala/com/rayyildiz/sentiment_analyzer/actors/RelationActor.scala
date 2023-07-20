package com.rayyildiz.sentiment_analyzer.actors
import akka.actor.{Actor, ActorRef}
import com.google.cloud.language.v1.Document.Type
import com.google.cloud.language.v1.{Document, EncodingType, LanguageServiceClient}

import scala.collection.JavaConverters._

class RelationActor(client: LanguageServiceClient, reply: ActorRef) extends Actor {
  import RelationActor._
  import com.rayyildiz.sentiment_analyzer.models.TextModels.DeterminationToken

  override def receive: Receive = {
    case text: String =>
      val doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build()
      val response = client.analyzeSyntax(doc, EncodingType.UTF8)

      val sentences = response.getSentencesList.asScala.map { sentences =>
        DeterminationSentence(sentences.getText.getContent, sentences.getText.getBeginOffset)
      }.toList

      val tokens = response.getTokensList.asScala.map { token =>
        val pos = token.getPartOfSpeech
        DeterminationToken(
          lemma = token.getLemma,
          tag = pos.getTag.name(),
          number = Option(pos.getNumber.name()),
          person = Option(pos.getPerson.name()),
          mood = Option(pos.getMood.name()),
          tense = Option(pos.getTense.name())
        )
      }.toList

      reply ! DeterminationRelationResult(sentences, tokens)

    case _ => sender() ! "Not implemented for other types"
  }
}

object RelationActor {
  import com.rayyildiz.sentiment_analyzer.models.TextModels.DeterminationToken

  case class DeterminationSentence(content: String, beginOffset: Int)
  case class DeterminationRelationResult(sentences: List[DeterminationSentence], tokens: List[DeterminationToken])

}
