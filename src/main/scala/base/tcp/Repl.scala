package base.tcp

import akka.stream.scaladsl.{Flow, Framing, Source, Tcp}
import akka.util.ByteString
import base.TcpRunnableAkkaApp

import scala.concurrent.Future
import scala.io.StdIn.readLine

object Repl extends TcpRunnableAkkaApp {

  def runIt: Future[Tcp.OutgoingConnection] = {
    val connection = Tcp().outgoingConnection("localhost", 1234)

    val replParser =
      Flow[String].takeWhile(_ != "q").concat(Source.single("BYE")).map(elem => ByteString(s"$elem\n"))

    val repl = Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
      .map(_.utf8String)
      .map(text => println("Server: " + text))
      .map(_ => readLine("> "))
      .via(replParser)

    connection.join(repl).run()
  }

}
