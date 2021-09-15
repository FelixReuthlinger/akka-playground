package base.tweets

import akka.Done
import akka.stream.scaladsl.Sink
import base.SimpleRunnableAkkaApp
import base.tweets.TweetData.{akkaTag, tweets}

import scala.concurrent.Future

object Tweets extends SimpleRunnableAkkaApp {

  def runIt: Future[Done] = {
    tweets
      .filterNot(_.hashtags.contains(akkaTag)) // Remove all base.tweets containing #akka hashtag
      .map(_.hashtags) // Get all sets of hashtags ...
      .reduce(_ ++ _) // ... and reduce them to a single set, removing duplicates across all base.tweets
      .mapConcat(identity) // Flatten the set of hashtags to a stream of hashtags
      .map(_.name.toUpperCase) // Convert all hashtags to upper case
      .runWith(Sink.foreach(println)) // Attach the Flow to a Sink that will finally print the hashtags
  }
}
