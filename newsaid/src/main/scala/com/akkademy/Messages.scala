package com.akkademy

case class ParseArticle(uri: String)
case class ParseHtmlArticle(uri: String, htmlString: String)
case class HttpResponse(body: String)
case class ArticleBody(uri: String, body: String)
case class ParseHtmlStringArticle(htmlString: String)
