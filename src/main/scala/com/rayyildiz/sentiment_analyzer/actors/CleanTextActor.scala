package com.rayyildiz.sentiment_analyzer.actors

import akka.actor.{Actor, ActorRef}
import org.jsoup.Jsoup

class CleanTextActor(reply: ActorRef) extends Actor {
  import CleanTextActor._

  override def receive: Receive = {
    case text: String =>
      val document = Jsoup.parse(text)
      val cleanedText = document.text()
      reply ! CleanedText(cleanedText)
    case _ => sender() ! "Not implemented for other types"
  }
}

object CleanTextActor {
  case class CleanedText(text: String)
}
