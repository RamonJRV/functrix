package io.functionalzen.functrix

import org.scalatest.AsyncFlatSpec
import org.scalatest.Matchers

class FallbackSpecifications
  extends AsyncFlatSpec
  with Matchers {

  import Fallback.fallbackValue

  behavior of "fallbackValue"

  val excFunc : Int => Int =
    (i) => {
      throw new Exception("excFunc Exception")
      i
    }

  import Func.createFuncFromFunction

  val defaultValue = 42


  it should "never throw exception from downstream Func" in {

    val func =
      for {
        f <- excFunc
        fallback <- fallbackValue(defaultValue)(f)
      } yield fallback

    func(0) map { res =>
      assert(res == defaultValue)
    }
  }

  it should "always return value from downstream Func if it is valid" in {
    val goodFunc : Int => Int = identity[Int]

    val func =
      for {
        f <- goodFunc
        fallback <- fallbackValue(defaultValue)(f)
      } yield fallback

    val testValue = 0

    func(testValue) map { res =>
      assert(res == testValue)
    }
  }

}//end class FallbackProperties

//31337
