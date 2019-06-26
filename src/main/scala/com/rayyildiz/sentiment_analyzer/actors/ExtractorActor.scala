package com.rayyildiz.sentiment_analyzer.actors
import akka.actor.{Actor, ActorRef}
import com.google.cloud.language.v1.Document.Type
import com.google.cloud.language.v1.{AnalyzeEntitiesRequest, Document, EncodingType, LanguageServiceClient}
import com.rayyildiz.sentiment_analyzer.models.{ExtractedEntity, ExtractedWords}
import scala.collection.JavaConverters._

class ExtractorActor(client: LanguageServiceClient, reply: ActorRef) extends Actor {

  override def receive: Receive = {
    case text: String =>
      val doc     = Document.newBuilder().setContent(text).setType(Type.PLAIN_TEXT).build()
      val request = AnalyzeEntitiesRequest.newBuilder().setDocument(doc).setEncodingType(EncodingType.UTF8).build()

      val list = client
        .analyzeEntities(request)
        .getEntitiesList
        .asScala
        .map { entity =>
          ExtractedEntity(word = entity.getName, entityType = entity.getType.toString, salience = entity.getSalience)
        }
        .toList

      reply ! ExtractedWords(entities = list)
    case _ => sender() ! "Not implemented for other types"
  }

}
