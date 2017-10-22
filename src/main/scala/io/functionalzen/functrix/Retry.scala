package io.functionalzen.functrix

import java.time.LocalDateTime
import java.time.{Duration => JavaDuration}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import Functrix.FunctrixOutput

object Retry {

  case class MaxRetryException[I](i : I,
                                  message : String = "",
                                  cause : Throwable = null) extends Exception(message, cause)

  def retryNtimes[I,O](n : Int)
                      (f : (I) => FunctrixOutput[O])
                      (implicit ec : ExecutionContext) : Functrix[I,O] = {
    val countDown = () => {
      val counter = (Iterator single true) ++ (Iterator from (n, -1) map (_ > 0))

      () => counter.next
    }

    genericRetry[I,O](countDown)(f)
  }

  private implicit def durationToJavaDuration(duration : Duration) : JavaDuration =
    JavaDuration ofMillis duration.toMillis

  def retryUntil[I,O](stopTime : LocalDateTime)
                     (f : (I) => FunctrixOutput[O])
                     (implicit ec : ExecutionContext) : Functrix[I,O] =
    genericRetry[I,O](() => {() => LocalDateTime.now isBefore stopTime})(f)

  def retryFor[I,O](duration : Duration)
                   (f : (I) => FunctrixOutput[O])
                   (implicit ec : ExecutionContext) : Functrix[I,O] =
    retryUntil(LocalDateTime.now plus duration)(f)


  def genericRetry[I,O](shouldRetryGenerator : () => () => Boolean)
                       (f : (I) => FunctrixOutput[O])
                       (implicit ec : ExecutionContext) : Functrix[I,O] =
    (input: I) => {

      val shouldRetry = shouldRetryGenerator()

      def execRetry(i : I) : FunctrixOutput[O] =
        if (shouldRetry())
          f(i) recoverWith {
            case _ => execRetry(i)
          }
        else
          Future failed MaxRetryException(i)

      execRetry(input)
    }//end (input: I)

}//end object Retry
