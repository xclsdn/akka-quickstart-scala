package com.example


import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

object FSMApp extends  App{
  val guardian2: ActorSystem[String]=ActorSystem(HelloWorld2(),"helloworld2")
  guardian2 ! "hello"
  guardian2 ! "hello world"
  guardian2 ! "dont go"
  guardian2.terminate()
}

object HelloWorld2{
  def apply():Behaviors.Receive[String]=Behaviors.receive{(context,message)=>
    context.log.info(s"received message '$message'")
    gone
  }


  def gone():Behaviors.Receive[String]=
    Behaviors.receive{(context,message)=>
      context.log.info(s"did you say '$message'. Sorry I have to go")
      Behaviors.stopped
    }
}



