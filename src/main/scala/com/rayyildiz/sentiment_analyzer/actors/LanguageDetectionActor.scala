package com.rayyildiz.sentiment_analyzer.actors

import akka.actor.{Actor, ActorRef}
import com.google.cloud.translate.Translate

class LanguageDetectionActor(client: Translate, reply: ActorRef) extends Actor {
  import LanguageDetectionActor._

  override def receive: Receive = {
    case text: String =>
      val detection = client.detect(text)
      reply ! LanguageDetectedText(detection.getLanguage, detection.getConfidence)

    case _ => sender() ! "Not implemented for other types"

  }
}

object LanguageDetectionActor {
  case class LanguageDetectedText(language: String, confidence: Float)

}
