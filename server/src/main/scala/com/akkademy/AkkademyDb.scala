package com.akkademy

import akka.actor.{Actor, ActorSystem, Props, Status}
import akka.event.Logging
import com.akkademy.messages._

import scala.collection.mutable

class AkkademyDb extends Actor {
  val map = new mutable.HashMap[String, Object]
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case SetRequest(key, value) =>
      log.info("received SetRequest - key: {}, value: {}", key, value)
      map.put(key, value)
      sender() ! Status.Success
    case GetRequest(key) =>
      log.info("received GetRequest - key: {}", key)
      val response: Option[Object] = map.get(key)
      response match {
        case Some(x) => sender() ! x
        case None => sender() ! Status.Failure(new KeyNotFoundException(key))
      }
    case SetIfNotExists(key, value) =>
      if (!map.contains(key)) {
        map.put(key, value)
        sender() ! Status.Success(true)
      } else {
        sender() ! Status.Success(false)
      }
    case Delete(key) =>
      map.remove(key) match {
        case Some(_) => sender() ! Status.Success(true)
        case None => sender() ! Status.Success(false)
      }
    case o =>
      log.info("received unknown message: {}", o)
      Status.Failure(new ClassNotFoundException)
  }
}

object Main extends App {
  val system = ActorSystem("akkademy")
  system.actorOf(Props[AkkademyDb], name = "akkademy-db")
}
