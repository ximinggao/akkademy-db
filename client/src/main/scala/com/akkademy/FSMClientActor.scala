package com.akkademy

import akka.actor.FSM
import com.akkademy.StateContainerTypes.RequestQueue
import com.akkademy.messages.Request

sealed trait State

case object Disconnected extends State

case object Connected extends State

case object ConnectedAndPending extends State

case object Flush

case object ConnectedMsg

object StateContainerTypes {
  type RequestQueue = List[Request]
}

class FSMClientActor(address: String) extends FSM[State, RequestQueue] {
  private val remoteDb = context.system.actorSelection(address)

  startWith(Disconnected, List.empty[Request])

  when(Disconnected) {
    case Event(_: messages.Connected, container: RequestQueue) =>
      if (container.headOption.isEmpty)
        goto(Connected)
      else
        goto(ConnectedAndPending)
    case Event(x: Request, container: RequestQueue) =>
      remoteDb ! new messages.Connected
      stay using (container :+ x)
    case x =>
      println("uhh didn't quite get that: " + x)
      stay
  }

  when(Connected) {
    case Event(x: Request, container: RequestQueue) =>
      goto(ConnectedAndPending) using(container :+ x)
  }

  when(ConnectedAndPending) {
    case Event(Flush, container) =>
      remoteDb ! container
      goto(Connected) using Nil
    case Event(x: Request, container: RequestQueue) =>
      stay using (container :+ x)
  }

  initialize()
}
