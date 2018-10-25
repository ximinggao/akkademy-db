package com.akkademy

import akka.actor.{ActorSystem, Props}
import akka.routing.BalancingPool
import com.akkademy.TestHelper.TestCameoActor
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}

class BalacingPoolSpec extends FlatSpec with Matchers {
  val system = ActorSystem()

  "BalancingPool" should "do work concurrently" in {
    val p = Promise[String]()

    val workerRouter = system.actorOf(BalancingPool(8).props(Props(classOf[ArticleParseActor])),
      "balancing-pool-router")

    val cameoActor = system.actorOf(Props(new TestCameoActor(p)))

    (0 to 2000).foreach(_ => {
      workerRouter.tell(ParseHtmlStringArticle(TestHelper.file), cameoActor)
    })

    TestHelper.profile(Await.ready(p.future, 20 seconds), "ActorsInBalancingPool")
  }
}
