package base

import akka.actor.ActorSystem
import akka.stream.scaladsl.{RunnableGraph, Tcp}

import scala.concurrent.{ExecutionContextExecutor, Future}

trait SimplerClassName {
  def getSimplerClassName = getClass.getSimpleName.replaceAll("[^\\w]", "")
}

trait SimpleActorSystem extends SimplerClassName {
  implicit val system: ActorSystem = ActorSystem(getSimplerClassName)
  implicit val ec: ExecutionContextExecutor = system.dispatcher
}

trait SimpleRunnableAkkaApp extends App with SimpleActorSystem {

  def runIt: Future[_]

  runIt.onComplete(_ => system.terminate())
}

trait PrintResultRunnableAkkaApp extends App with SimpleActorSystem {

  def runIt: Future[Any]

  val result: Future[Any] = runIt
  result.foreach(c => println(s"Result: ${c.toString}"))
  result.onComplete(_ => system.terminate())
}

trait GraphRunnableAkkaApp extends App with SimpleActorSystem {
  def buildGraph: RunnableGraph[_]

  buildGraph.run()
}

trait TcpRunnableAkkaApp extends App with SimpleActorSystem {
  def runIt: Future[Tcp.OutgoingConnection]

  val result: Future[Tcp.OutgoingConnection] = runIt
  //result.onComplete(_ => system.terminate())
}
