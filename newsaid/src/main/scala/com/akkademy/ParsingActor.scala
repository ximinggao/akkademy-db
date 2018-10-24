package com.akkademy

import akka.actor.Actor
import de.l3s.boilerpipe.extractors.ArticleExtractor

class ParsingActor extends Actor {
  override def receive: Receive = {
    case ParseHtmlArticle(key, html) =>
      sender() ! ArticleBody(key, ArticleExtractor.INSTANCE.getText(html))
    case x =>
      println("unknown message " + x.getClass)
  }
}
