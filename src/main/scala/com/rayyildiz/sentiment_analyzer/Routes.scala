package com.rayyildiz.sentiment_analyzer

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.rayyildiz.sentiment_analyzer.models.ApplicationModels.ErrorMessage
import com.rayyildiz.sentiment_analyzer.models.JsonSerialization
import com.rayyildiz.sentiment_analyzer.routes.{MiddlewareRoutes, RestRoutes}
import javax.inject.Inject

import scala.concurrent.ExecutionContext

class Routes @Inject()(
    private val middlewareRoutes: MiddlewareRoutes,
    private val restRoutes: RestRoutes
)(private implicit val materializer: ActorMaterializer, implicit val executionContext: ExecutionContext)
    extends JsonSerialization {

  val exceptionHandler = ExceptionHandler {
    case t: Throwable => {
      extractUri { _ =>
        complete(ErrorMessage(StatusCodes.InternalServerError.intValue, t.getMessage))
      }
    }
  }

  val jsonRejectionHandler: RejectionHandler = RejectionHandler
    .newBuilder()
    .handleAll[Rejection] { rejections => (context: RequestContext) =>
      {
        val route: StandardRoute = reject(rejections: _*)
        route.apply(context).map { res: RouteResult =>
          res
        }
      }
    }
    .handleNotFound {
      complete(ErrorMessage(StatusCodes.NotFound.intValue, s"Request not found"))
    }
    .result()

  def apply(): Route =
    handleRejections(jsonRejectionHandler) {
      handleExceptions(exceptionHandler) {
        middlewareRoutes() ~ restRoutes()
      }
    }
}
