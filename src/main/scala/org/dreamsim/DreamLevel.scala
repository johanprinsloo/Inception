package org.dreamsim

import actors.Actor
import actors.Actor._
import scala.collection.mutable.Set
import grizzled.slf4j._
import javax.sound.sampled.Line.Info

/**
 * Implements Inception dreamlevel with these charateristics:
 *    time resolution is in seconds
 */
class DreamLevel(name: String,
                 creator: Character,
                 mazeComplexity: Double = 1.0) extends Actor with Logging {

  val characters: Set[Character] = Set.empty
  val projections: Set[Projection] = Set.empty
  var realizer: Character = Scenario.environment
  var previouslevel: DreamLevel = Scenario.reality
  var nextlevel: DreamLevel = Scenario.reality
  var level = 0L
  var aggression: Double = 0.5 //projection aggression level
  var time = 0
  info(name + " designed by " + creator)

  /**
   * A specific character has to realize the dream level - this character's subconscious drives the
   * level
   */
  def realize(from: Character,
              previousDL: DreamLevel,
              trainingLevel: Double = 0.5): DreamLevel = {
    realizer = from
    level = previousDL.increment
    previouslevel = previousDL
    aggression = trainingLevel
    info(name + " dreamed by " + from)
    return this
  }

  def act = eventloop {
    case timeinc: TimeTick => timeIncrement(timeinc)
    case character: Character => <--(character)
  }

  def timeIncrement(timetick: TimeTick) = {
    characters.par foreach {
      ch => ch !! TimeTick
    }
    projections foreach {
      ch => ch !! TimeTick
    }
    nextlevel ! new TimeTick(timetick.increment * Scenario.levelTimeMultiplier)
    info(name + " time advanced by " + timetick.increment)
  }

  // join character to dream
  def <--(character: Character): DreamLevel =  {
    characters += character
    if (character == realizer) {
      projections ++= character.getProjections(mazeComplexity)
      aggression = character.trainingLevel
    }
    debug(character + " joined dream " + name)
    return this
  }

  // remove character from dream
  def -->(character: Character): DreamLevel = {
    characters -= character
    return this
  }

  def increment: Long = {
    level + 1
  }

  def dreamname : String = name
}