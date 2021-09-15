package base.tweets

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink}
import base.PrintResultRunnableAkkaApp
import base.tweets.TweetData.tweets

import scala.concurrent.Future

object MaterializedTweets extends PrintResultRunnableAkkaApp {
  override def runIt: Future[Int] = {
    val count: Flow[Tweet, Int, NotUsed] = Flow[Tweet].map(_ => 1)

    val sumSink: Sink[Int, Future[Int]] = Sink.fold[Int, Int](0)(_ + _)

    val counterGraph: RunnableGraph[Future[Int]] =
      tweets.via(count).toMat(sumSink)(Keep.right)

    counterGraph.run()
  }
}
