// See LICENSE for license details.

package treadle

import firrtl.CommonOptions
import org.scalatest.{FreeSpec, Matchers}


// scalastyle:off magic.number
class VecSpec extends FreeSpec with Matchers {
  "simple register chain should work" in {
    val input =
      """
        |circuit ShiftRegisterTester :
        |  module ShiftRegisterTester :
        |    input clock : Clock
        |    input reset : UInt<1>
        |    output out : UInt<16>
        |
        |    reg counter : UInt<3>, clock with : (reset => (reset, UInt<3>("h00"))) @[Counter.scala 26:33]
        |    node _T_8 = add(counter, UInt<1>("h01")) @[Counter.scala 35:22]
        |    node _T_9 = tail(_T_8, 1) @[Counter.scala 35:22]
        |    counter <= _T_9 @[Counter.scala 35:13]
        |
        |    reg shifter : UInt<16>[4], clock @[Vec.scala 110:20]
        |    shifter[0] <= shifter[1] @[Vec.scala 111:46]
        |    shifter[1] <= shifter[2] @[Vec.scala 111:46]
        |    shifter[2] <= shifter[3] @[Vec.scala 111:46]
        |    shifter[3] <= counter @[Vec.scala 112:16]
        |    out <= shifter[0]
        |
      """.stripMargin

    val optionsManager = new InterpreterOptionsManager {
      treadleOptions = treadleOptions.copy(
        writeVCD = true,
        setVerbose = true,
        showFirrtlAtLoad = true
      )
    }

    val tester = new TreadleTester(input, optionsManager)
//
//    tester.poke("reset", 1)
//    tester.step()
//    tester.poke("reset", 0)

    def show(): Unit = {
      for(name <- Seq("shifter_3", "shifter_2", "shifter_1", "shifter_0")) {
        println(s"${tester.engine.renderComputation(name)}")
      }
    }

    tester.step()
    show()
    tester.step()
    show()
    tester.step()
    show()
    tester.step()
    show()
    tester.step()
    show()
    tester.step()
    show()
    tester.step()
    show()
    tester.step()
  }

  "VecSpec should pass a basic test" in {
    val input =
      """
        |circuit ShiftRegisterTester :
        |  module ShiftRegisterTester :
        |    input clock : Clock
        |    input reset : UInt<1>
        |    output io : {}
        |
        |    reg value : UInt<3>, clock with : (reset => (reset, UInt<3>("h00"))) @[Counter.scala 26:33]
        |    when UInt<1>("h01") : @[Counter.scala 63:17]
        |      node _T_6 = eq(value, UInt<3>("h07")) @[Counter.scala 34:24]
        |      node _T_8 = add(value, UInt<1>("h01")) @[Counter.scala 35:22]
        |      node _T_9 = tail(_T_8, 1) @[Counter.scala 35:22]
        |      value <= _T_9 @[Counter.scala 35:13]
        |      skip @[Counter.scala 63:17]
        |    node wrap = and(UInt<1>("h01"), _T_6) @[Counter.scala 64:20]
        |    reg shifter : UInt<2>[4], clock @[Vec.scala 110:20]
        |    shifter[0] <= shifter[1] @[Vec.scala 111:46]
        |    shifter[1] <= shifter[2] @[Vec.scala 111:46]
        |    shifter[2] <= shifter[3] @[Vec.scala 111:46]
        |    shifter[3] <= value @[Vec.scala 112:16]
        |    node _T_20 = geq(value, UInt<3>("h04")) @[Vec.scala 113:13]
        |    when _T_20 : @[Vec.scala 113:26]
        |      node _T_22 = sub(value, UInt<3>("h04")) @[Vec.scala 114:24]
        |      node _T_23 = asUInt(_T_22) @[Vec.scala 114:24]
        |      node _T_24 = tail(_T_23, 1) @[Vec.scala 114:24]
        |      node _T_25 = eq(shifter[0], _T_24) @[Vec.scala 115:23]
        |      node _T_26 = bits(reset, 0, 0) @[Vec.scala 115:11]
        |      node _T_27 = or(_T_25, _T_26) @[Vec.scala 115:11]
        |      node _T_29 = eq(_T_27, UInt<1>("h00")) @[Vec.scala 115:11]
        |      when _T_29 : @[Vec.scala 115:11]
        |        printf(clock, UInt<1>(1), "Assertion failed\n    at Vec.scala:115 assert(shifter(0) === expected)\n") @[Vec.scala 115:11]
        |        stop(clock, UInt<1>(1), 1) @[Vec.scala 115:11]
        |        skip @[Vec.scala 115:11]
        |      skip @[Vec.scala 113:26]
        |    when wrap : @[Vec.scala 117:15]
        |      node _T_30 = bits(reset, 0, 0) @[Vec.scala 118:9]
        |      node _T_32 = eq(_T_30, UInt<1>("h00")) @[Vec.scala 118:9]
        |      when _T_32 : @[Vec.scala 118:9]
        |        stop(clock, UInt<1>(1), 0) @[Vec.scala 118:9]
        |        skip @[Vec.scala 118:9]
        |      skip @[Vec.scala 117:15]
        |
      """.stripMargin

    val optionsManager = new InterpreterOptionsManager {
      treadleOptions = treadleOptions.copy(
        writeVCD = true,
        setVerbose = true
      )
    }

    val tester = new TreadleTester(input, optionsManager)

    tester.poke("reset", 1)
    tester.step()
    tester.poke("reset", 0)

    println(s"value is ${tester.engine.renderComputation("value")}")
    tester.step()
    println(s"value is ${tester.engine.renderComputation("value")}")
    tester.step()
    println(s"value is ${tester.engine.renderComputation("value")}")
    tester.step()
    println(s"value is ${tester.engine.renderComputation("value")}")
    tester.step()
    println(s"value is ${tester.engine.renderComputation("value")}")
    tester.step()
    println(s"value is ${tester.engine.renderComputation("value")}")
    tester.step()
    println(s"value is ${tester.engine.renderComputation("value")}")
    tester.step()
    println(s"value is ${tester.engine.renderComputation("value")}")
    tester.step()
  }
}