package com.rayyildiz.sentiment_analyzer.controllers

import javax.inject.{Inject, Singleton}

import com.rayyildiz.sentiment_analyzer.models.{ApplicationInformation, ApplicationStatus}

import scala.concurrent.ExecutionContext

@Singleton
class MiddlewareController @Inject()()(implicit val executionContext: ExecutionContext) {

  def health: ApplicationStatus = ApplicationStatus(status = true, name = "sentiment_analyzer")

  def info: ApplicationInformation = ApplicationInformation(version = "1.1", name = "sentiment_analyzer")

}
