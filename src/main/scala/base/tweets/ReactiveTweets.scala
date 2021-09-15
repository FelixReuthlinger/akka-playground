package base.tweets

import akka.stream.scaladsl.{Sink, Source}
import akka.{Done, NotUsed}
import base.SimpleRunnableAkkaApp
import base.tweets.TweetData.{akkaTag, tweets}

import scala.concurrent.Future

object ReactiveTweets extends SimpleRunnableAkkaApp {
  override def runIt: Future[Done] = {
    val authors: Source[Author, NotUsed] =
      tweets.filter(_.hashtags.contains(akkaTag)).map(_.author)
    authors.runWith(Sink.foreach(println))
  }
}
