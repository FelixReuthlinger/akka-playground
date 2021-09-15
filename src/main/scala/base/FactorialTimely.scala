package base

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}
import akka.util.ByteString
import akka.{Done, NotUsed}

import java.nio.file.Paths
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object FactorialTimely extends SimpleRunnableAkkaApp {

  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String].map(s => ByteString(s + "\n")).toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  def runIt: Future[Done] = {
    val source: Source[Int, NotUsed] = Source(1 to 100)
    val factorials = source.scan(BigInt(1))((acc, next) => acc * next)
    factorials
      .zipWith(Source(0 to 100))((num, idx) => s"$idx! = $num")
      .throttle(1, 1.second)
      .runForeach(println)
  }
}
