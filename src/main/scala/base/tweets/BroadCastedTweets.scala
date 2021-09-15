package base.tweets

import akka.NotUsed
import akka.stream.scaladsl.{Broadcast, FileIO, Flow, GraphDSL, Keep, RunnableGraph, Sink}
import akka.stream.{ClosedShape, IOResult}
import akka.util.ByteString
import base.GraphRunnableAkkaApp
import base.tweets.TweetData.tweets

import java.nio.file.Paths
import scala.concurrent.Future

object BroadCastedTweets extends GraphRunnableAkkaApp {

  def lineSink(filename: String): Sink[Product, Future[IOResult]] =
    Flow[Product].map(s => ByteString(s.toString + "\n")).toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  override def buildGraph: RunnableGraph[NotUsed] = {
    val writeAuthors: Sink[Product, Future[IOResult]] = lineSink("authors.txt")
    val writeHashtags: Sink[Product, Future[IOResult]] = lineSink("hashtags.txt")
    RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
      import GraphDSL.Implicits._

      val broadcast = b.add(Broadcast[Tweet](2))
      tweets ~> broadcast.in
      broadcast.out(0) ~> Flow[Tweet].map(_.author) ~> writeAuthors
      broadcast.out(1) ~> Flow[Tweet].mapConcat(_.hashtags.toList) ~> writeHashtags
      ClosedShape
    })
  }
}
