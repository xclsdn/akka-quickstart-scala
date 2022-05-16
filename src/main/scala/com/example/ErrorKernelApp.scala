package com.example

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object ErrorKernelApp extends App {
  val guardian: ActorSystem[Guardian.Start] = ActorSystem(Guardian(), "error-kernel")
  guardian ! Guardian.Start(List("clean-the-room", "refill"))
}

object Guardian {

  sealed trait Command

  case class Start(tasks: List[String]) extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      val manager: ActorRef[Manager.Command] =
        context.spawn(Manager(), "manager")
      Behaviors.receiveMessage {
        case Start(tasks) =>
          manager ! Manager.Delegate(tasks)
          Behaviors.same
      }
    }
}

object Manager {

  sealed trait Command

  final case class Delegate(tasks: List[String]) extends Command

  final case class Report(task: String)

  private case class WorkerDoneAdapter(response: Worker.Response) extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      val adapter: ActorRef[Worker.Response] =
        context.messageAdapter(rsp => WorkerDoneAdapter(rsp))

      Behaviors.receiveMessage { msg =>
        msg match {
          case Delegate(tasks) =>
            tasks.map { task =>
              val worker: ActorRef[Worker.Command] =
                context.spawn(Worker(), s"worker-$task")
              context.log.info(s"sending task '$task' to $worker")
              worker ! Worker.Do(adapter, task)
            }
            Behaviors.same
          case WorkerDoneAdapter(Worker.Done(task)) =>
            context.log.info(s"task '$task' has been finished")
            Behaviors.same
        }
      }
    }

}

object Worker {
  sealed trait Command

  final case class Do(replyTo: ActorRef[Worker.Response], task: String) extends Command

  sealed trait Response

  final case class Done(task: String) extends Response

  def apply(): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case Do(replyTo, task) =>
          context.log.info(s"'${context.self.path}'. Done with '$task'")
          replyTo ! Worker.Done(task)
          Behaviors.stopped
      }

    }
}