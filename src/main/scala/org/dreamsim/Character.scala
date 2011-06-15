package org.dreamsim

import scala.actors._
import Actor._
import collection.immutable.Stack
import scala.Double
import grizzled.slf4j._

class Totem(name : String ) {
  var state = true
}

class Character( name: String, totem: Option[Totem] ) extends Actor with Logging {

  var sane = true
  var consciousness = Stack( Scenario.reality )
  var trainingLevel: Double = 0.5
  var sedationlevel: Double = 0.0
  var time = 0L
  info( name + " created ")

  def act = eventloop {
    case shot : Sedation => sedationlevel = shot.level
    case sleep: Sleep => sleeprules( sleep.dream )
    case Kick => kickrules
    case Kill => killrules
    case TimeTick => {}
  }

  def createDreamlevel( name: String , mazeComplexity: Double = 0.5): DreamLevel =
        new DreamLevel( name, this, mazeComplexity )

  def realizeDreamlevel( dl : DreamLevel ): DreamLevel =
        dl.realize(this, consciousness.top, trainingLevel)

  /**
   * the Totem will indicate reality
   */
  def totemCheck: Boolean = {
    return !totem.isEmpty && ( consciousness.size == 1 ) && sane
  }

  def sleeprules( dream: DreamLevel ) = {
    consciousness push dream
    dream <-- this
  }

  def killrules = {
    sedationlevel match {
      case d:Double if d >= Scenario.criticalSedationLevel => { // drop to limbo
      }
      case d:Double if d < Scenario.criticalSedationLevel => { // kick up a level
        val dreamlevel = consciousness top
      }
    }
  }

  def kickrules = {
    (consciousness.pop2)._1 --> ( this )
    info( name + " kicked up to " + (consciousness.top).dreamname )
  }

  def ticktime( tick: TimeTick ) = {
    time = time + tick.increment
    debug( name + " dream time advanced to " + time )
  }


  def getProjections( mazeComplexity: Double ) : Set[Projection] = {
    Set.empty
  }


  //add charater to the dream
  def --> ( dream: DreamLevel ) : Character = {
    consciousness push dream
    dream <-- this
    return this
  }

  //remove character from the dream
  def <-- ( dream : DreamLevel ) : DreamLevel = {
    consciousness top match {
      case `dream` =>  consciousness pop; dream --> this
      case _ => error("cannot remove chracter ") + name + " from dream " + dream
    }
    return dream
  }

}

object Character {
  def apply ( name: String, totem: Option[Totem] ):  Character = new Character( name, totem )
  def apply ( name: String ): Character =  new Character( name, None )
  def apply ( ): Character = new Character("Jane", None)

  def totembyname( name: String, totemname: Option[String] ): Character = {
    val totem: Option[ Totem ] = totemname match {
      case None => None
      case Some(name) => Option( new Totem(name) )
    }
    new Character(name, totem )
  }
}

class Projection( name : String ) extends Actor {
  var time = 0L
  def act = eventloop {
    case TimeTick => timeTick
    case Kill => { exit() }
  }

  def timeTick = {
    //all actions per second
    time = time + 1
  }
}