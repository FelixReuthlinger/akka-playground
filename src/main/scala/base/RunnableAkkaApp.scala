package base

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.RunnableGraph

import scala.concurrent.{ExecutionContextExecutor, Future}

trait SimpleRunnableAkkaApp extends App {

  def runIt: Future[_]

  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.replaceAll("[^\\w]", ""))
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  runIt.onComplete(_ => system.terminate())
}

trait GraphRunnableAkkaApp extends App {
  def buildGraph: RunnableGraph[_]

  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.replaceAll("[^\\w]", ""))
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  buildGraph.run()
}
