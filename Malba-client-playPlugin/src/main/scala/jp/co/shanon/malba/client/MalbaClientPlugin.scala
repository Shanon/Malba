package jp.co.shanon.malba.client
import play.api.Play
import play.api.libs.concurrent.Akka 
import play.api.libs.concurrent.Execution.Implicits._
import play.api.Plugin
import play.api.Application
import play.api.Logger
import akka.actor.ActorRef
import akka.actor.ActorSystem
import scala.concurrent._
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

class MalbaClientPlugin(implicit app: Application) extends Plugin {
  private val from : String = app.configuration.getString("malbaClient.from").getOrElse(throw new RuntimeException("Need to set 'malbaClient.from' configuration."))
  private val timeout : FiniteDuration = Duration(app.configuration.getInt("malbaClient.timeout.seconds").getOrElse(5), SECONDS)
  private var maxRetry : Int = app.configuration.getInt("malbaClient.timeout.seconds").getOrElse(8)
  lazy val system = ActorSystem("MalbaClient", ConfigFactory.load.getConfig("MalbaClient"))

  lazy val client = new MalbaClient(system, from, timeout, maxRetry)

  override def onStart() = {
    Logger.info("[MalbaClientPlugin] initializing: %s".format(this.getClass))
    ()
  }
  override def onStop() = {
    Logger.info("[MalbaClientPlugin] stopping: %s".format(this.getClass))
    system.shutdown()
    system.awaitTermination()
    ()
  }
}

object MalbaClientPlugin {
  def apply(implicit app: Application): MalbaClient = {
    app.plugin[MalbaClientPlugin] match {
      case Some(plugin) => plugin.client
      case None => throw new RuntimeException("There is no MalbaClientPlugin registerd.")
    }
  }
}
