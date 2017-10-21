package io.functionalzen.functrix


import Func.{FuncOutput, wrapOutput}

import scala.concurrent.ExecutionContext


object Fallback {

  final def fallbackValue[I,O](default : O)
                              (f : (I) => FuncOutput[O])
                              (implicit ec : ExecutionContext) : Func[I,O] =
    fallback(() => default)(f)

  final def fallback[I,O](default : () => O)
                         (f : (I) => FuncOutput[O])
                         (implicit ec : ExecutionContext): Func[I, O] =
    (input: I) =>
      f(input) fallbackTo {
        wrapOutput(default())
      }

}//end object Fallback

//31337
