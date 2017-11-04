package io.functionalzen.functrix

import Functrix.FunctrixOutput

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{ExecutionContext, Future}
import akka.actor.Scheduler
import akka.pattern.after
import io.functionalzen.functrix.event.TimeoutEvent

object Timeout {

  case class FunctrixTimeoutException[I](i : I,
                                         message : String = "",
                                         cause : Throwable = null) extends Exception(message, cause)

  def timeoutAfter[I,O](duration : FiniteDuration)
                       (f : (I) => FunctrixOutput[O])
                       (implicit ec : ExecutionContext,
                        scheduler: Scheduler,
                        em : EventMonitor) : Functrix[I,O] = {
    val timer : I => FunctrixOutput[O] =
      (i) => after(duration, using = scheduler)(Future failed FunctrixTimeoutException(i))

    genericTimeout(timer)(f)
  }

  def genericTimeout[I,O](timer : (I) => FunctrixOutput[O])
                         (f : (I) => FunctrixOutput[O])
                         (implicit ec : ExecutionContext,
                          em : EventMonitor) : Functrix[I,O] =
    (input: I) =>
      Future
        .firstCompletedOf(Seq(timer(input), f(input)))
        .recoverWith {
          case timeoutEx : FunctrixTimeoutException[_] =>
            em update TimeoutEvent
            Future failed timeoutEx
        }

}//end object Timeout

//31337
