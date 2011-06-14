package org.dreamsim

import java.lang.Double

object Scenario {

  val environment = new Character("environment", None )
  val criticalSedationLevel = 0.5d  //level above which a death will lead to limbo instead of a kick up
  val levelTimeMultiplier = 20
  val reality = new DreamLevel("reality", environment, Double.NaN)
  val limbo = new DreamLevel("limbo", environment, 1e6)

}