package base.tcp

import akka.Done
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.stream.scaladsl._
import akka.util.ByteString
import base.SimpleRunnableAkkaApp

import scala.concurrent.Future

object SimpleAnswer extends SimpleRunnableAkkaApp {

  def runIt: Future[Done] = {
    val connections: Source[IncomingConnection, Future[ServerBinding]] =
      Tcp().bind("localhost", 1234)
    connections.runForeach { connection =>
      println(s"New connection from: ${connection.remoteAddress}")

      val echo = Flow[ByteString]
        .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
        .map(_.utf8String)
        .map(_ + "!!!\n")
        .map(ByteString(_))

      connection.handleWith(echo)
    }
  }
}

