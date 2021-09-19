package base

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{RunnableGraph, Tcp}

import scala.concurrent.{ExecutionContextExecutor, Future}

trait SimpleRunnableAkkaApp extends App {

  def runIt: Future[_]

  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.replaceAll("[^\\w]", ""))
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  runIt.onComplete(_ => system.terminate())
}

trait PrintResultRunnableAkkaApp extends App {

  def runIt: Future[Any]

  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.replaceAll("[^\\w]", ""))
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  val result: Future[Any] = runIt
  result.foreach(c => println(s"Result: ${c.toString}"))
  result.onComplete(_ => system.terminate())
}

trait GraphRunnableAkkaApp extends App {
  def buildGraph: RunnableGraph[_]

  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.replaceAll("[^\\w]", ""))
  implicit val ec: ExecutionContextExecutor = system.dispatcher
  buildGraph.run()
}

trait TcpRunnableAkkaApp extends App {
  def runIt: Future[Tcp.OutgoingConnection]

  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.replaceAll("[^\\w]", ""))
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  val result: Future[Tcp.OutgoingConnection] = runIt
  //result.onComplete(_ => system.terminate())
}
