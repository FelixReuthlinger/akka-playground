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

object BetterAnswer extends SimpleRunnableAkkaApp {
  override def runIt: Future[Done] = {
    val connections: Source[IncomingConnection, Future[ServerBinding]] =
      Tcp().bind("localhost", 1234)

    connections.runForeach(connection => {
      // server logic, parses incoming commands
      val commandParser = Flow[String].takeWhile(_ != "BYE").map(_ + "!")

      import connection._
      val welcomeMsg = s"Welcome to: $localAddress, you are: $remoteAddress!"
      val welcome = Source.single(welcomeMsg)

      val serverLogic = Flow[ByteString]
        .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
        .map(_.utf8String)
        .via(commandParser)
        // merge in the initial banner after parser
        .merge(welcome)
        .map(_ + "\n")
        .map(ByteString(_))

      connection.handleWith(serverLogic)
    })
  }
}

