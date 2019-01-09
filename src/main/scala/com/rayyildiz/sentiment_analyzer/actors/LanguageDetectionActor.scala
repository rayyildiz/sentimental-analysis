package com.rayyildiz.sentiment_analyzer.actors

import akka.actor.{Actor, ActorRef}
import com.google.cloud.translate.Translate
import com.rayyildiz.sentiment_analyzer.models.LanguageDetectedText

class LanguageDetectionActor(client: Translate, reply: ActorRef) extends Actor {
  override def receive: Receive = {
    case text: String =>
      val detection = client.detect(text)
      reply ! LanguageDetectedText(detection.getLanguage, detection.getConfidence)

    case _ => sender() ! "Not implemented for other types"

  }
}
