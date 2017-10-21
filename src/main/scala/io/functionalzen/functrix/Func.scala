package io.functionalzen.functrix

import scala.concurrent.{ExecutionContext, Future}

object Func {

  type FuncOutput[O] = Future[O]

  final def wrapOutput[O](o : O) : FuncOutput[O] = Future successful o

  implicit def createFuncFromFunction[I,O](f : (I) => O)
                                          (implicit ec : ExecutionContext) : Func[I,O] =
    (input: I) => Future(f(input))

}//end object Func


trait Func[I,O] { self =>

  import Func.FuncOutput


  def apply(input : I) : FuncOutput[O]

  final def map[P](f : (I => FuncOutput[O]) => I => FuncOutput[P]): Func[I, P] =
    (input: I) => f(self.apply)(input)

  final def flatMap[P](f : (I => FuncOutput[O]) => Func[I,P]) : Func[I,P] = f(self.apply)


}//end trait Func

//31337
