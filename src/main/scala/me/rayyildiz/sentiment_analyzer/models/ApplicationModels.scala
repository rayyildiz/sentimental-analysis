package me.rayyildiz.sentiment_analyzer.models

case class Ping(message: String)

case class Pong(message: String)

// Error Message
case class ErrorMessage(statusCode: Int, error: String)
