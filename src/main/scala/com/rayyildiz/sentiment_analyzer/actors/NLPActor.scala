package com.rayyildiz.sentiment_analyzer.actors

import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import akka.actor.{Actor, OneForOneStrategy, Props, SupervisorStrategy}
import com.google.cloud.language.v1.LanguageServiceClient
import com.google.cloud.translate.Translate

import scala.concurrent.TimeoutException
import scala.concurrent.duration._

class NLPActor(languageApi: LanguageServiceClient, translateApi: Translate) extends Actor {
  import com.rayyildiz.sentiment_analyzer.models.TextModels._

  override def receive: Receive = {
    case ExtractWord(text) =>
      val child = context.actorOf(Props(new ExtractorActor(languageApi, sender())))
      child ! text

    case SentimentWord(text) =>
      val child = context.actorOf(Props(new SentimentActor(languageApi, sender())))
      child ! text

    case DeterminationRelationWord(text) =>
      val child = context.actorOf(Props(new RelationActor(languageApi, sender())))
      child ! text

    case CleanText(text) =>
      val child = context.actorOf(Props(new CleanTextActor(sender())))
      child ! text

    case LanguageDetectText(text) =>
      val child = context.actorOf(Props(new LanguageDetectionActor(translateApi, sender())))
      child ! text

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
  def apply(languageApi: LanguageServiceClient, translateApi: Translate): Props = Props.create(NLPActor.getClass, languageApi, translateApi)
}
