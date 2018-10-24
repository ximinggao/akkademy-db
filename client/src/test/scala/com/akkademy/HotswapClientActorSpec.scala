package com.akkademy

import akka.actor.{ActorRef, ActorSystem, Props, Status}
import akka.testkit.TestProbe
import akka.util.Timeout
import com.akkademy.messages.{GetRequest, SetRequest}
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.duration._

class HotswapClientActorSpec extends FunSpec with Matchers {
  implicit val system: ActorSystem = ActorSystem("LocalSystem")
  implicit val timeout: Timeout = Timeout(5 seconds)

  val client: ActorRef = system.actorOf(Props.create(classOf[HotswapClientActor], "127.0.0.1:2552"))
  describe("HowswapClientActor") {
    it("should set/get values") {
      val probe = TestProbe()

      client ! SetRequest("testkey", "testvalue", probe.ref)
      probe.expectMsg(Status.Success)

      client ! GetRequest("testkey", probe.ref)
      probe.expectMsg("testvalue")
    }
  }
}
