package com.akkademy

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Status}
import akka.event.Logging
import com.akkademy.messages._

import scala.collection.mutable

class AkkademyDb extends Actor {
  val map = new mutable.HashMap[String, Object]
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case x: Connected => sender() ! x
    case x: List[_] =>
      x.foreach {
        case SetRequest(key, value, senderRef) =>
          handleSetRequest(key, value, senderRef)
        case SetIfNotExists(key, value, senderRef) =>
          handleSetIfNotExistsRequest(key, value, senderRef)
        case GetRequest(key, senderRef) =>
          handleGetRequest(key, senderRef)
        case Delete(key, senderRef) =>
          handleDeleteRequest(key, senderRef)
      }
    case SetRequest(key, value, senderRef) =>
      handleSetRequest(key, value, senderRef)
    case SetIfNotExists(key, value, senderRef) =>
      handleSetIfNotExistsRequest(key, value, senderRef)
    case GetRequest(key, senderRef) =>
      handleGetRequest(key, senderRef)
    case Delete(key, senderRef) =>
      handleDeleteRequest(key, senderRef)
    case o =>
      log.info("received unknown message: {}", o)
      sender() ! Status.Failure(new ClassNotFoundException)
  }

  private def handleSetRequest(key: String, value: Object, senderRef: ActorRef): Unit = {
    log.info("received SetRequest - key: {}, value: {}", key, value)
    map.put(key, value)

    (if (senderRef == ActorRef.noSender) sender() else senderRef) ! Status.Success
  }

  private def handleGetRequest(key: String, senderRef: ActorRef): Unit = {
    log.info("received GetRequest - key: {}", key)
    val response = map.get(key)
    response match {
      case Some(x) => (if (senderRef == ActorRef.noSender) sender() else senderRef) ! x
      case None => (if (senderRef == ActorRef.noSender) sender() else senderRef) ! Status.Failure(KeyNotFoundException(key))
    }
  }

  private def handleSetIfNotExistsRequest(key: String, value: Object, senderRef: ActorRef): Unit = {
    if (!map.contains(key)) {
      map.put(key, value)
      (if (senderRef == ActorRef.noSender) sender() else senderRef) ! Status.Success(true)
    } else {
      (if (senderRef == ActorRef.noSender) sender() else senderRef) ! Status.Success(false)
    }
  }

  private def handleDeleteRequest(key: String, senderRef: ActorRef): Unit = {
    map.remove(key) match {
      case Some(_) => (if (senderRef == ActorRef.noSender) sender() else senderRef) ! Status.Success(true)
      case None => (if (senderRef == ActorRef.noSender) sender() else senderRef) ! Status.Success(false)
    }
  }
}

object Main extends App {
  val system = ActorSystem("akkademy")
  system.actorOf(Props[AkkademyDb], name = "akkademy-db")
}
