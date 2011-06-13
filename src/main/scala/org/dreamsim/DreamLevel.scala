package org.dreamsim

import actors.Actor
import actors.Actor._

/**
 * Implements Inception dreamlevel with these charateristics:
 *    time resolution is in seconds
 */
class DreamLevel(name: String,
                 creator: Character,
                 mazeComplexity: Double = 1.0) extends Actor {

  val characters : Set[Character] = Set.empty
  val projections : Set[Projection] = Set.empty
  var realizer: Character = Scenario.environment
  var previouslevel: DreamLevel = Scenario.reality
  var level = 0L
  var aggression: Double = 0.5  //projection aggression level
  var time = 0

  /**
   * A specific character has to realize the dream level - this character's subconscious drives the
   * level
   */
  def realize( from: Character,
               previousDL :  DreamLevel,
               trainingLevel:  Double = 0.5 ): DreamLevel = {
    realizer = from
    level = previousDL.increment
    previouslevel = previousDL
    aggression = trainingLevel
    return this
  }

  def act = eventloop {
    case timeinc : TimeTick => timeincrement( timeinc )
  }

  def timeincrement( timetick: TimeTick ) = {
     characters foreach  { ch => ch !! TimeTick }
     projections foreach  { ch => ch !! TimeTick }
  }

 def increment : Long = {
   level + 1
 }
}