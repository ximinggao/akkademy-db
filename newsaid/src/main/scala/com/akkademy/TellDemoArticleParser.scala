package com.akkademy

import akka.actor.Status.Failure
import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import akka.util.Timeout
import com.akkademy.messages.{GetRequest, SetRequest}

import scala.concurrent.{ExecutionContextExecutor, TimeoutException}

class TellDemoArticleParser(cacheActorPath: String,
                            httpClientActorPath: String,
                            articleParserActorPath: String,
                            implicit val timeout: Timeout) extends Actor {
  val cacheActor: ActorSelection = context.actorSelection(cacheActorPath)
  val httpClientActor: ActorSelection = context.actorSelection(httpClientActorPath)
  val articleParserActor: ActorSelection = context.actorSelection(articleParserActorPath)

  implicit val ec: ExecutionContextExecutor = context.dispatcher

  private def buildExtraActor(senderRef: ActorRef, uri: String): ActorRef = {
    context.actorOf(Props(new Actor {
      override def receive: Receive = {
        case "timeout" =>
          senderRef ! Failure(new TimeoutException("timeout!"))
          context.stop(self)

        case HttpResponse(body) =>
          articleParserActor ! ParseHtmlArticle(uri, body)

        case body: String =>
          senderRef ! body
          context.stop(self)

        case ArticleBody(url, body) =>
          cacheActor ! SetRequest(url, body)
          senderRef ! body
          context.stop(self)

        case t =>
          println("ignoring msg: " + t.getClass)
      }
    }))
  }

  override def receive: Receive = {
    case ParseArticle(uri) =>
      val extraActor = buildExtraActor(sender(), uri)
      cacheActor.tell(GetRequest(uri), extraActor)
      httpClientActor.tell(uri, extraActor)

      context.system.scheduler.scheduleOnce(timeout.duration, extraActor, "timeout")
  }
}
