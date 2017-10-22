package io.functionalzen.functrix

import io.functionalzen.functrix.Functrix.FunctrixOutput

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object CircuitBreaker {

  case class CircuitBreakerOpenException(message : String = "",
                                         cause : Throwable = null) extends Exception(message, cause)

  def outputExceptionBreaker[I,O](f : (I) => FunctrixOutput[O])
                                 (implicit ec : ExecutionContext) : Functrix[I,O] = {
    var breakerClosed = true

    val outputBreaker : FunctrixOutput[O] => FunctrixOutput[O] =
      _.andThen {
        case Failure(_) => breakerClosed = false
       }
       .transformWith {
         case Success(o) =>
           if(breakerClosed)
             Future successful o
           else
             Future failed CircuitBreakerOpenException()

         case Failure(ex) =>
           Future failed ex
       }

    genericCircuitBreaker((_ : I) => breakerClosed, outputBreaker)(f)
  }


  def genericCircuitBreaker[I,O](inputBreaker : I => Boolean = (_: I) => true,
                                 outputBreaker : FunctrixOutput[O] => FunctrixOutput[O] = identity[FunctrixOutput[O]] _)
                                (f : (I) => FunctrixOutput[O])
                                (implicit ec : ExecutionContext) : Functrix[I, O] =
    (input: I) =>
      if (inputBreaker(input)) {
        outputBreaker(f(input))
      }
      else {
        Functrix wrapException CircuitBreakerOpenException()
      }

}//end object CircuitBreaker

//31337

