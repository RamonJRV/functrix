package io.functionalzen.functrix

import scala.concurrent.Future

object Func {

  type FuncOutput[O] = Future[O]

}//end object Func

/**
  * Created by ramonromeroyvigil on 10/21/17.
  */
trait Func[I,O] { self =>

  import Func.FuncOutput


  def apply(input : I) : FuncOutput[O]

  final def map[P](f : (I => FuncOutput[O]) => I => FuncOutput[P]): Func[I, P] = new Func[I, P] {
    override def apply(input: I) : FuncOutput[P] = f(self.apply)(input)
  }

  final def flatMap[P](f : (I => FuncOutput[O]) => Func[I,P]) : Func[I,P] = f(self.apply)


}//end trait Func

//31337
