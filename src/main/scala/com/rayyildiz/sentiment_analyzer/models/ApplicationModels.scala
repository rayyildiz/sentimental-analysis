package com.rayyildiz.sentiment_analyzer.models

case class Ping(message: String)

case class Pong(message: String)

// Error Message
case class ErrorMessage(statusCode: Int, error: String)

case class ApplicationStatus(status: Boolean, name: String)

case class ApplicationInformation(version: String, name: String)
