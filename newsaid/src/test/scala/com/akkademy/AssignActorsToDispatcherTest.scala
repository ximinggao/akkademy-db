package com.akkademy

import akka.actor.{ActorSystem, Props}
import akka.routing.RoundRobinGroup
import com.akkademy.TestHelper.TestCameoActor
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}

class AssignActorsToDispatcherTest extends FlatSpec with Matchers {
  val system = ActorSystem()

  "ActorsAssignedToDispatcher" should "do work concurrently" in {
    val p = Promise[String]()

    val actors = (0 to 7).map(_ => {
      system.actorOf(Props(classOf[ArticleParseActor]).withDispatcher("article-parsing-dispatcher"))
    })

    val workerRouter = system.actorOf(RoundRobinGroup(
      actors.map(x => x.path.toStringWithoutAddress).toList).props(),
      "workerRounter"
    )

    val cameoActor = system.actorOf(Props(new TestCameoActor(p)))

    (0 to 2000).foreach(_ => {
      workerRouter.tell(ParseHtmlStringArticle(TestHelper.file), cameoActor)
    })

    TestHelper.profile(Await.ready(p.future, 20 seconds), "ActorsAssignedToDispatcher")
  }
}
