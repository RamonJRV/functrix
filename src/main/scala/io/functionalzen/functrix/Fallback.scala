package io.functionalzen.functrix


import Functrix.{FunctrixOutput, wrapOutput}

import scala.concurrent.ExecutionContext


object Fallback {

  final def fallbackValue[I,O](default : O)
                              (f : (I) => FunctrixOutput[O])
                              (implicit ec : ExecutionContext) : Functrix[I,O] =
    genericFallback(() => default)(f)

  final def genericFallback[I,O](default : () => O)
                                (f : (I) => FunctrixOutput[O])
                                (implicit ec : ExecutionContext): Functrix[I, O] =
    (input: I) =>
      f(input) fallbackTo {
        wrapOutput(default())
      }

}//end object Fallback

//31337
