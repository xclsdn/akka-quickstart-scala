package com.example


import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

import scala.io.StdIn

object MinimalCluster extends App{
  val guardian=ActorSystem(Behaviors.empty,"minimal")
  StdIn.readLine()
  guardian.terminate()
}