package com.akkademy.messages

import akka.actor.ActorRef

trait Request

case class GetRequest(key: String, sender: ActorRef = ActorRef.noSender) extends Request

case class SetRequest(key: String, value: Object, sender: ActorRef = ActorRef.noSender) extends Request

case class SetIfNotExists(key: String, value: Object, sender: ActorRef = ActorRef.noSender) extends Request

case class Delete(key: String, sender: ActorRef = ActorRef.noSender) extends Request

case class Connected()

case class KeyNotFoundException(key: String) extends Exception
