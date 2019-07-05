package com.rayyildiz.sentiment_analyzer.controllers

import javax.inject.Singleton
import com.rayyildiz.sentiment_analyzer.models.ApplicationModels.{ApplicationInformation, ApplicationStatus, Ping, Pong}

@Singleton
class ApplicationController {

  def ping(ping: Ping): Pong = Pong(ping.message)

  def health: ApplicationStatus = ApplicationStatus(status = true, name = "sentiment_analyzer")

  def info: ApplicationInformation = ApplicationInformation(version = "1.1", name = "sentiment_analyzer")
}
