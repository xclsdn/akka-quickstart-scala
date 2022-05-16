package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import scala.io.StdIn


object HelloWorldApp extends App {
  val guardian: ActorSystem[String] =
    ActorSystem(HelloWorld(), "helloWorld")
  guardian ! "hello"
  guardian ! "hello again"

  println("press enter to stop")
  StdIn.readLine()
  guardian.terminate()
}

object HelloWorld {

  def apply(): Behavior[String] =
    Behaviors.setup { context =>
      context.log.info("this onley happens once")

      Behaviors.receiveMessage { message =>
        context.log.info(s"received message '$message'")

        Behaviors.same
      }
    }
}