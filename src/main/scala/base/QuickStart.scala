package base

import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}

import scala.concurrent.Future

object QuickStart extends SimpleRunnableAkkaApp {

  def runIt: Future[Done] = {
    val source: Source[Int, NotUsed] = Source(1 to 100)
    val done: Future[Done] = source.runForeach(i => println(i))
    done
  }
}
