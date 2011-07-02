package org.dreamsim

import actors.Actor
import actors.Actor._
import scala.collection.mutable.Set
import grizzled.slf4j._
import scala.None

/**
 * Implements Inception dreamlevel with these charateristics:
 *    time resolution is in seconds
 */
class DreamLevel(val name: String,
                 val creator: Character,
                 val mazeComplexity: Double = 1.0) extends Actor with Logging {

  val characters: Set[Character] = Set.empty
  val projections: Set[Projection] = Set.empty
  var realizer: Character = Scenario.environment
  var previouslevel: Option[DreamLevel] = None //(Scenario.reality)
  var nextlevel: Option[DreamLevel] = None //Some(Scenario.reality)
  var level = 0L
  var aggression: Double = 0.5 //projection aggression level
  var time = 0L
  info( name + " designed by " + creator.name )

  /**
   * A specific character has to realize the dream level - this character's subconscious drives the
   * level
   */
  def realize(from: Character,
              previousDL: DreamLevel,
              trainingLevel: Double = 0.5): DreamLevel = {
    realizer = from
    level = previousDL.increment
    previouslevel = Some(previousDL)
    aggression = trainingLevel
    previousDL.linkDownLevel( this )
    info(name + " dreamed by " + from.name)
    return this
  }

  def linkDownLevel( downlevel: DreamLevel ) = {
    nextlevel = Some(downlevel)
  }

  def act = eventloop {
    case timeinc: TimeTick => { timeIncrement( timeinc ); reply() }
    case character: Character => {this <-- character; reply() }
  }

  def timeIncrement(timetick: TimeTick) = {

    time = time + timetick.increment
    info( name + " time incremented by " + timetick.increment + " to " + time )
    characters foreach {
      ch => if( ch.consciousness.top == this ) ch !? TimeTick( timetick.increment )
    }
    projections foreach {
      ch => ch !? TimeTick
    }

    nextlevel match {
      case Some(x) => { x !? new TimeTick( timetick.increment * Scenario.levelTimeMultiplier ) }
      case None => debug("no nextlevel defined in " + name)
    }

    debug(name + " time advanced by " + timetick.increment)
  }

  // join character to dream
  def introduceRealizer(character: Character) {
    projections ++= character.getProjections(mazeComplexity)
    aggression = character.trainingLevel
  }

  def introduceDreamer(character: Character) {
    projections ++= character.getPsychoticProjections(mazeComplexity)
  }

  def <-- (character: Character): DreamLevel =  {
    characters += character

    if( character == realizer ) introduceRealizer( character )
    else introduceDreamer( character )

//    character match {
//      case `realizer` => introduceRealizer( character )
//      case _ => introduceDreamer( character )
//    }
    debug(character + " joined dream " + name)
    return this
  }

  // remove character from dream
  def --> (character: Character): DreamLevel = {
    characters -= character
    return this
  }

  def increment: Long = {
    level + 1
  }
}

object DreamLevel {
  def apply ( name : String, creator: Character, mazeComplexity :  Double = 0.5 ) : DreamLevel = {
    val dl = new DreamLevel( name, creator, mazeComplexity )
    dl.start
    return dl
  }
}