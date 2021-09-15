package base.tweets

import akka.stream.scaladsl.{Sink, Source}
import akka.{Done, NotUsed}
import base.SimpleRunnableAkkaApp
import base.tweets.TweetData.tweets

import scala.concurrent.Future

object FlatHashtags extends SimpleRunnableAkkaApp {
  override def runIt: Future[Done] = {
    val hashtags: Source[Hashtag, NotUsed] = tweets.mapConcat(_.hashtags.toList)
    hashtags.runWith(Sink.foreach(println))
  }
}
