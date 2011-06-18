package org.dreamsim


object Scenario {

  val environment = new Character("environment", None )
  val criticalSedationLevel = 0.5d  //level above which a death will lead to limbo instead of a kick up
  val levelTimeMultiplier = 20
  val reality = DreamLevel("reality", environment, Double.NaN)
  val limbo = new DreamLevel("limbo", environment, 1e6)
  private var scenetime : Long = 0L


  def getSceneTime :  Long = scenetime
}