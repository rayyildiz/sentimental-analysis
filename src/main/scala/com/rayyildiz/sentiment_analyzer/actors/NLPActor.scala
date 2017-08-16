package com.rayyildiz.sentiment_analyzer.actors

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import com.google.cloud.language.v1beta2.Document.Type
import com.google.cloud.language.v1beta2.{AnalyzeEntitiesRequest, Document, EncodingType, LanguageServiceClient}
import com.rayyildiz.sentiment_analyzer.models._

import scala.collection.JavaConverters._
import scala.concurrent.TimeoutException
import scala.concurrent.duration._

class NLPActor extends Actor {

  lazy val languageApi: LanguageServiceClient = LanguageServiceClient.create()

  override def receive: Receive = {
    case ExtractWord(text) => {
      val doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build()
      val request = AnalyzeEntitiesRequest.newBuilder().setDocument(doc).setEncodingType(EncodingType.UTF8).build()

      val list = languageApi
        .analyzeEntities(request)
        .getEntitiesList
        .asScala
        .map { entity =>
          ExtractedEntity(word = entity.getName, entityType = entity.getType.toString, salience = entity.getSalience)
        }
        .toList

      sender() ! ExtractedWords(entities = list)
    }
    case SentimentWord(text) => {
      val doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build()
      val response = languageApi.analyzeSentiment(doc)

      val documentSentiment = response.getDocumentSentiment

      val list = response.getSentencesList.asScala.map { str =>
        val sentiment = str.getSentiment
        val feelingText: String = if (sentiment.getScore > 0) "POSITIVE" else "NEGATIVE"

        SentimentedSentences(
          sentence = str.getText.getContent,
          score = sentiment.getScore,
          magnitude = sentiment.getMagnitude,
          feeling = feelingText
        )

      }.toList

      val feelingText: String = if (documentSentiment.getScore > 0) "POSITIVE" else "NEGATIVE"

      sender() ! SentimentedWords(
        magnitude = documentSentiment.getMagnitude,
        score = documentSentiment.getScore,
        documentFeeling = feelingText,
        entities = list
      )
    }

    case DeterminationRelationWord(text) => {
      val doc = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build()
      var response = languageApi.analyzeSyntax(doc, EncodingType.UTF8)

      val sentences = response.getSentencesList.asScala.map { sentecnces =>
        DeterminationSentence(sentecnces.getText.getContent(), sentecnces.getText.getBeginOffset)
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

      sender() ! DeterminationRelationResult(sentences, tokens)
    }
    case _ => throw new NotImplementedError("Not implemented for other types")
  }

  // scalastyle:off magic.number
  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 60 seconds) {
      case _: TimeoutException    => Restart
      case _: NotImplementedError => Resume
      case t: Throwable           => super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }

  // scalastyle:on magic.number
}

object NLPActor {
  def props: Props = Props(classOf[NLPActor])
}
