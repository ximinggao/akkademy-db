package com.akkademy

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Status}
import akka.testkit.{TestActorRef, TestProbe}
import akka.util.Timeout
import com.akkademy.messages.{Delete, SetIfNotExists, SetRequest}
import org.scalatest.{FunSpecLike, Matchers}

class AkkademyDbSpec extends FunSpecLike with Matchers {
  implicit val system: ActorSystem = ActorSystem()
  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  describe("akkademyDb") {
    describe("given SetRequest") {
      it("should place key/value into map") {
        val actorRef = TestActorRef(new AkkademyDb)
        actorRef ! SetRequest("key", "value")

        val akkademyDb = actorRef.underlyingActor
        akkademyDb.map.get("key") should equal(Some("value"))
      }
    }

    describe("given SetIfNotExists") {
      it("should place key/value into map if not exists") {
        val probe = TestProbe()
        val dbActorRef = TestActorRef(new AkkademyDb)
        dbActorRef.tell(SetIfNotExists("key", "value"), probe.ref)

        val db = dbActorRef.underlyingActor
        db.map.get("key") should equal(Some("value"))
        var response = probe.expectMsgType[Status.Success]
        response.status should equal(true)

        dbActorRef.tell(SetIfNotExists("key", "value"), probe.ref)
        response = probe.expectMsgType[Status.Success]
        response.status should equal(false)
      }
    }

    describe("given Delete") {
      it("should delete key from map if exists") {
        val probe = TestProbe()
        val dbActorRef = TestActorRef(new AkkademyDb)
        dbActorRef ! SetRequest("key", "value")

        val db = dbActorRef.underlyingActor
        db.map.get("key") should equal(Some("value"))

        dbActorRef.tell(Delete("key"), probe.ref)
        var response = probe.expectMsgType[Status.Success]
        response.status should equal(true)

        dbActorRef.tell(Delete("key"), probe.ref)
        response = probe.expectMsgType[Status.Success]
        response.status should equal(false)
      }
    }
  }
}
