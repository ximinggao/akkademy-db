package com.akkademy

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Status}
import akka.testkit.{TestActorRef, TestProbe}
import akka.util.Timeout
import com.akkademy.messages.{Delete, SetIfNotExists, SetRequest}
import com.typesafe.config.ConfigFactory
import org.scalatest.{FunSpecLike, Matchers}

class AkkademyDbSpec extends FunSpecLike with Matchers {
  implicit val system: ActorSystem = ActorSystem("system", ConfigFactory.empty)
  implicit val timeout: Timeout = Timeout(5, TimeUnit.SECONDS)

  describe("akkademyDb") {
    describe("given SetRequest") {
      it("should place key/value into map") {
        val testProbe = TestProbe()

        val actorRef = TestActorRef(new AkkademyDb)
        actorRef ! SetRequest("key", "value", testProbe.ref)

        val akkademyDb = actorRef.underlyingActor
        akkademyDb.map.get("key") should equal(Some("value"))
      }
    }

    describe("given SetIfNotExists") {
      it("should place key/value into map if not exists") {
        val probe = TestProbe()
        val dbActorRef = TestActorRef(new AkkademyDb)
        dbActorRef ! SetIfNotExists("key", "value", probe.ref)

        val db = dbActorRef.underlyingActor
        db.map.get("key") should equal(Some("value"))
        var response = probe.expectMsgType[Status.Success]
        response.status should equal(true)

        dbActorRef ! SetIfNotExists("key", "value", probe.ref)
        response = probe.expectMsgType[Status.Success]
        response.status should equal(false)
      }
    }

    describe("given Delete") {
      it("should delete key from map if exists") {
        val probe = TestProbe()
        val dbActorRef = TestActorRef(new AkkademyDb)
        dbActorRef ! SetRequest("key", "value", probe.ref)
        probe.expectMsg(Status.Success)

        val db = dbActorRef.underlyingActor
        db.map.get("key") should equal(Some("value"))

        dbActorRef ! Delete("key", probe.ref)
        var response = probe.expectMsgType[Status.Success]
        response.status should equal(true)

        dbActorRef ! Delete("key", probe.ref)
        response = probe.expectMsgType[Status.Success]
        response.status should equal(false)
      }
    }

    describe("given List[SetRequest]") {
      it("should place key/value into map") {
        val testProbe = TestProbe()
        val actorRef = TestActorRef(new AkkademyDb)
        actorRef ! List(
          SetRequest("key", "value", testProbe.ref),
          SetRequest("key2", "value2", testProbe.ref)
        )

        val akkademyDb = actorRef.underlyingActor
        akkademyDb.map.get("key") should equal(Some("value"))
        akkademyDb.map.get("key2") should equal(Some("value2"))
        testProbe.expectMsg(Status.Success)
        testProbe.expectMsg(Status.Success)
      }
    }
  }
}
