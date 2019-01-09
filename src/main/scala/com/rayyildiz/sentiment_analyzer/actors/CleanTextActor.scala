package com.rayyildiz.sentiment_analyzer.actors

import akka.actor.{Actor, ActorRef}
import com.rayyildiz.sentiment_analyzer.models.CleanedText
import org.jsoup.Jsoup

class CleanTextActor(reply: ActorRef) extends Actor {
  override def receive: Receive = {
    case text: String =>
      val document = Jsoup.parse(text)
      val cleanedText = document.text()
      reply ! CleanedText(cleanedText)
    case _ => sender() ! "Not implemented for other types"
  }
}
