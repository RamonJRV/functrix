package io.functionalzen.functrix

import scala.concurrent.{ExecutionContext, Future}

object Functrix {

  type FuncOutput[O] = Future[O]

  final def wrapOutput[O](o : O) : FuncOutput[O] = Future successful o

  implicit def createFuncFromFunction[I,O](f : (I) => O)
                                          (implicit ec : ExecutionContext) : Functrix[I,O] =
    (input: I) => Future(f(input))

}//end object Func


trait Functrix[I,O] { self =>

  import Functrix.FuncOutput


  def apply(input : I) : FuncOutput[O]

  final def map[P](f : (I => FuncOutput[O]) => I => FuncOutput[P]): Functrix[I, P] =
    (input: I) => f(self.apply)(input)

  final def flatMap[P](f : (I => FuncOutput[O]) => Functrix[I,P]) : Functrix[I,P] = f(self.apply)


}//end trait Func

//31337
