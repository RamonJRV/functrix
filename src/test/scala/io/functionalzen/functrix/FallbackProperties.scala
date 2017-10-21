package io.functionalzen.functrix

import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers

class FallbackProperties
  extends AsyncFlatSpec
  with Matchers {

  import Fallback.fallbackValue

  behavior of "fallbackValue"

  val excFunc : Int => Int =
    (i) => {
      throw(new Exception("excFunc Exception"))
      i
    }

  import Func.createFuncFromFunction

  it should "never throw exception from downstream Func" in {
    val defaultValue = 42

    val func =
      for {
        f <- excFunc
        fallback <- fallbackValue(defaultValue)(f)
      } yield fallback

    func(0) map { res =>
      assert(res == defaultValue)
    }
  }

}//end class FallbackProperties

//31337
