package base.tcp

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{BidiFlow, Flow, Sink, Source, Tcp}
import akka.util.ByteString
import akka.util.ByteString.UTF_8
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

/**
 * code from https://stackoverflow.com/questions/36242183/how-to-implement-a-simple-tcp-protocol-using-akka-streams
 */

trait CommonLogic {
  implicit val system: ActorSystem = ActorSystem("akka-stream-tcp-chatter", ConfigFactory.defaultReference())

  type Message = String
  val (host, port) = ("localhost", 12345)

  val deserialize: ByteString => Message = _.utf8String
  val serialize: Message => ByteString = message => ByteString(message getBytes UTF_8)

  val incoming: Flow[ByteString, Message, _] = Flow fromFunction deserialize
  val outgoing: Flow[Message, ByteString, _] = Flow fromFunction serialize

  val protocol: BidiFlow[ByteString, Message, Message, ByteString, NotUsed] = BidiFlow.fromFlows(incoming, outgoing)

  def prompt(s: String): Source[Message, _] = Source fromIterator {
    () => Iterator.continually(StdIn readLine s"[$s]> ")
  }

  val print: Sink[Message, _] = Sink foreach println
}

object AkkaStreamTcpChatterClient extends App with CommonLogic {

  Tcp()
    .outgoingConnection(host, port)
    .join(protocol)
    .runWith(prompt("C").async, print)
}

object AkkaStreamTcpChatterServer extends App with CommonLogic {

  Tcp()
    .bind(host, port)
    .runForeach {
      _
        .flow
        .join(protocol)
        .runWith(prompt("S").async, print)
    }
}