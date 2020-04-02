/*
Copyright 2020 The Regents of the University of California (Regents)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package treadle.blackboxes

import firrtl.stage.FirrtlSourceAnnotation
import org.scalatest.{FreeSpec, Matchers}
import treadle.{BlackBoxFactoriesAnnotation, TreadleTester, WriteVcdAnnotation}

// scalastyle:off magic.number
class ClockDividerSpec extends FreeSpec with Matchers {
  "clock dividers 2 and 3 creates signals that are 1/2 and 1/3 the frequency of the main clock" in {
    val input =
      """
        |;buildInfoPackage: chisel3, version: 3.2-SNAPSHOT, scalaVersion: 2.12.6, sbtVersion: 1.2.6
        |circuit UsesClockDivider2and3 :
        |  extmodule ClockDivider2 :
        |    input clk_in : Clock
        |    output clk_out : UInt<1>
        |
        |    defname = ClockDivider2
        |
        |  extmodule ClockDivider3 :
        |    input clk_in : Clock
        |    output clk_out : UInt<1>
        |
        |    defname = ClockDivider3
        |
        |  module UsesClockDivider2and3 :
        |    input clock : Clock
        |    output main_clock : UInt<1>
        |    output divided_clock_2 : UInt<1>
        |    output divided_clock_3 : UInt<1>
        |
        |    inst divider2 of ClockDivider2
        |    inst divider3 of ClockDivider3
        |
        |    divider2.clk_in <= clock
        |    main_clock <= asUInt(clock)
        |
        |    divided_clock_2 <= asUInt(divider2.clk_out)
        |
        |
        |    divider3.clk_in <= clock
        |    divided_clock_3 <= asUInt(divider3.clk_out)
        |
      """.stripMargin

    val options = Seq(
      WriteVcdAnnotation,
      BlackBoxFactoriesAnnotation(Seq(new BuiltInBlackBoxFactory))
    )

    val tester = TreadleTester(FirrtlSourceAnnotation(input) +: options)

    for (trial <- 0 to 20) {
      tester.expect("main_clock", BigInt(0))
      tester.expect("divided_clock_2", trial % 2)
      tester.expect("divided_clock_3", if (trial % 3 == 2) 0 else 1)

      tester.step()
    }

    tester.finish
  }
}
