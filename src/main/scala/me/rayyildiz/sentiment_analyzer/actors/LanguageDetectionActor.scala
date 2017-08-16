package me.rayyildiz.sentiment_analyzer.actors

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import com.google.cloud.translate.TranslateOptions
import me.rayyildiz.sentiment_analyzer.models.LanguageDetection

import scala.concurrent.TimeoutException
import scala.concurrent.duration._

class LanguageDetectionActor extends Actor {

  lazy val translate = TranslateOptions
    .newBuilder()
    .setProjectId("flux-1102")
    .setCredentials(getGoogleCredentials) // FIXME not required any more, use environment variable
    .build()
    .getService

  override def receive: Receive = {
    case text: String =>
      val detection = translate.detect(text)
      sender() ! LanguageDetection(detection.getLanguage, detection.getConfidence)
    case _ => throw new NotImplementedError("Not implemented for other types")
  }

  // scalastyle:off magic.number
  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 30 seconds) {
      case _: TimeoutException    => Restart
      case _: NotImplementedError => Resume
      case t: Throwable           => super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
    }

  // scalastyle:on magic.number
}

object LanguageDetectionActor {
  def props: Props = Props(classOf[LanguageDetectionActor])
}
