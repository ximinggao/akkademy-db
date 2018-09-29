package com.akkademy

import com.akkademy.messages.KeyNotFoundException
import org.scalatest.{FunSpecLike, Matchers}

import scala.concurrent.duration._
import scala.concurrent.Await

class SClientIntegrationSpec extends FunSpecLike with Matchers {
  val client = new SClient("127.0.0.1:2552")
  describe("akkademyDbClient") {
    it("should set a value") {
      client.set("123", new Integer(123))
      val futureResult = client.get("123")
      val result = Await.result(futureResult, 10 seconds)
      result should equal(123)
    }

    it("should effect on key not exists") {
      val future = client.get("nonexists")
      intercept[KeyNotFoundException] {
        Await.result(future, 1 second)
      }
    }
  }
}
