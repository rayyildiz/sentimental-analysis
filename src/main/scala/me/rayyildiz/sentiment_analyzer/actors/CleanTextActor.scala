package me.rayyildiz.sentiment_analyzer.actors

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import me.rayyildiz.sentiment_analyzer.models.CleanedText
import org.jsoup.Jsoup

import scala.concurrent.TimeoutException
import scala.concurrent.duration._

class CleanTextActor extends Actor {
  override def receive: Receive = {
    case text: String =>
      val document = Jsoup.parse(text)
      val cleanedText = document.text()
      sender() ! CleanedText(cleanedText)
    case _ => throw new NotImplementedError("Not implemented for other types")
  }

  // scalastyle:off magic.number
  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 5, withinTimeRange = 30 seconds) {
      case _: TimeoutException    => Restart
      case _: NotImplementedError => Resume
      case t: Throwable           => super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }

  // scalastyle:on magic.number
}

object CleanTextActor {
  def props: Props = Props(classOf[CleanTextActor])
}
