package com.akkademy

import akka.actor.{Actor, Stash}
import com.akkademy.messages.{Connected, Request}

class HotswapClientActor(address: String) extends Actor with Stash {
  private val remoteDb = context.system.actorSelection(s"akka.tcp://akkademy@$address/user/akkademy-db")

  override def receive: Receive = {
    case x: Request =>
      remoteDb ! new Connected
      stash()
    case _: Connected =>
      unstashAll()
      context.become(online)
  }

  def online: Receive = {
    case x: Disconnected_ =>
      context.unbecome()
    case x: Request =>
      remoteDb forward x
  }

}

class Disconnected_
