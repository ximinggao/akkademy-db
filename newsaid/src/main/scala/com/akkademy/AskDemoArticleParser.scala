package com.akkademy

import akka.actor.Actor
import akka.actor.Status.Failure
import akka.util.Timeout
import akka.pattern.ask
import com.akkademy.messages.{GetRequest, SetRequest}

import scala.concurrent.Future

class AskDemoArticleParser(cacheActorPath: String,
                           httpClientActorPath: String,
                           articleParserActorPath: String,
                           implicit val timeout: Timeout) extends Actor {
  val cacheActor = context.actorSelection(cacheActorPath)
  val httpClientActor = context.actorSelection(httpClientActorPath)
  val articleParserActor = context.actorSelection(articleParserActorPath)

  import scala.concurrent.ExecutionContext.Implicits.global

  override def receive: Receive = {
    case ParseArticle(uri) =>
      val senderRef = sender()

      val cacheResult = cacheActor ? GetRequest(uri)

      val result = cacheResult.recoverWith {
        case _ : Exception =>
          val fRawResult = httpClientActor ? uri
          fRawResult flatMap {
            case HttpResponse(rawArticle) =>
              articleParserActor ? ParseHtmlArticle(uri, rawArticle)
            case x =>
              Future.failed(new Exception("unknown response"))
          }
      }

      result onComplete {
        case scala.util.Success(x: String) =>
          println("cached result!")
          senderRef ! x
        case scala.util.Success(x: ArticleBody) =>
          cacheActor ! SetRequest(uri, x.body)
          senderRef ! x
        case scala.util.Failure(exception) =>
          senderRef ! Failure(exception)
        case x =>
          println("unknown message! " + x)
      }
  }
}
