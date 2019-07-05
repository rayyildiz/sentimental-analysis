package com.rayyildiz.sentiment_analyzer.routes

import javax.inject.{Inject, Singleton}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.rayyildiz.sentiment_analyzer.controllers.ApplicationController
import com.rayyildiz.sentiment_analyzer.models.JsonSerialization

@Singleton
class MiddlewareRoutes @Inject()(
    private val applicationController: ApplicationController
) extends JsonSerialization {

  def apply(): Route =
    path("health") {
      get {
        complete(applicationController.health)
      }
    } ~
      path("info") {
        get {
          complete(applicationController.info)
        }
      }
}
