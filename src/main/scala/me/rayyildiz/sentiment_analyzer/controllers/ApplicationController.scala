package me.rayyildiz.sentiment_analyzer.controllers

import javax.inject.Singleton

import me.rayyildiz.sentiment_analyzer.models.{Ping, Pong}

@Singleton
class ApplicationController {
  def ping(ping: Ping): Pong = Pong(ping.message)
}
