package io.functionalzen.functrix

import akka.actor.Scheduler
import akka.pattern.after
import io.functionalzen.functrix.Func.FuncOutput

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

object Delay {

  def delay[I,O](duration: FiniteDuration, delayFirstRun : Boolean = false)
                (f : (I) => FuncOutput[O])
                (implicit ec : ExecutionContext, scheduler: Scheduler) : Func[I,O] = {
    val shouldDelay =
      (Iterator single delayFirstRun)  ++ (Iterator continually true)

    genericDelay(duration, (_ : I) => shouldDelay.next())(f)
  }


  def genericDelay[I,O](duration: FiniteDuration, shouldDelay : I => Boolean)
                       (f : (I) => FuncOutput[O])
                       (implicit ec : ExecutionContext, scheduler: Scheduler) : Func[I,O] =
    (input: I) =>
      if (shouldDelay(input))
        after(duration, scheduler)(f(input))
      else
        f(input)


}//end object Delay

//31337
