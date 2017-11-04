package io.functionalzen.functrix

import akka.actor.Scheduler
import akka.pattern.after
import io.functionalzen.functrix.Functrix.FunctrixOutput
import io.functionalzen.functrix.event.{ShouldDelayEvent, ShouldNotDelayEvent}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

object Delay {

  def delay[I,O](duration: FiniteDuration, delayFirstRun : Boolean = false)
                (f : (I) => FunctrixOutput[O])
                (implicit ec : ExecutionContext, scheduler: Scheduler, em : EventMonitor) : Functrix[I,O] = {
    val shouldDelay =
      (Iterator single delayFirstRun)  ++ (Iterator continually true)

    genericDelay(duration, (_ : I) => shouldDelay.next())(f)
  }


  def genericDelay[I,O](duration: FiniteDuration, shouldDelay : I => Boolean)
                       (f : (I) => FunctrixOutput[O])
                       (implicit ec : ExecutionContext,
                        scheduler: Scheduler,
                        em : EventMonitor) : Functrix[I,O] =
    (input: I) =>
      if (shouldDelay(input)) {
        em update ShouldDelayEvent
        after(duration, scheduler)(f(input))
      }
      else {
        em update ShouldNotDelayEvent
        f(input)
      }

}//end object Delay

//31337
