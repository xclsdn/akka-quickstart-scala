package com.example

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorSystem, Behavior}

object HelloWorld3 extends  App{
  val system=ActorSystem(HelloWorldWithType(),"hello")
  system ! HelloWorldWithType.Message("hi")
}

object HelloWorldWithType{
  final case class Message(content:String)

  def apply(): Behavior[Message] =
    Behaviors.receive[Message]{(context,msg)=>
      msg match{
        case Message(content) =>
          context.log.info(s"the mesage is ..... $content")
      }
      Behaviors.stopped
    }

}