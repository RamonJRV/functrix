package io.functionalzen.functrix

import scala.concurrent.{ExecutionContext, Future}

object Functrix {

  type FunctrixOutput[O] = Future[O]

  final def wrapOutput[O](o : O) : FunctrixOutput[O] = Future successful o

  implicit def createFunctrixFromFunction[I,O](f : (I) => O)
                                              (implicit ec : ExecutionContext) : Functrix[I,O] =
    (input: I) => Future(f(input))

  def wrapException[O](ex : Throwable) : FunctrixOutput[O] = Future failed ex

}//end object Func


trait Functrix[I,O] { self =>

  import Functrix.FunctrixOutput


  def apply(input : I) : FunctrixOutput[O]

  final def map[P](f : (I => FunctrixOutput[O]) => I => FunctrixOutput[P]): Functrix[I, P] =
    (input: I) => f(self.apply)(input)

  final def flatMap[P](f : (I => FunctrixOutput[O]) => Functrix[I,P]) : Functrix[I,P] = f(self.apply)


}//end trait Func

//31337
