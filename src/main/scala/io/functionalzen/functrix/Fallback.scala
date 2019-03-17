package io.functionalzen.functrix


import Functrix.{FunctrixOutput, wrapOutput}
import io.functionalzen.functrix.event.FallbackEvent

import scala.concurrent.ExecutionContext


object Fallback {

  final def fallbackValue[I,O](default : O)
                              (f : I => FunctrixOutput[O])
                              (implicit ec : ExecutionContext,
                               em : EventMonitor) : Functrix[I,O] =
    genericFallback((_ : I) => default)(f)

  final def genericFallback[I,O](default : I => O)
                                (f : I => FunctrixOutput[O])
                                (implicit ec : ExecutionContext, em : EventMonitor): Functrix[I, O] =
    (input: I) =>
      f(input) fallbackTo {
        em update FallbackEvent
        wrapOutput(default())
      }

}//end object Fallback

//31337
