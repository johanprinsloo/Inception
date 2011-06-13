package org.dreamsim

import scala.actors._
import Actor._
import java.lang.Boolean
import collection.immutable.Stack

class Totem(name : String ) {
  var state = true
}

class Character( name: String, totem: Option[Totem] ) extends Actor {

  var sane = true
  var consciousness = Stack( Scenario.reality )
  var trainingLevel: Double = 0.5
  var sedationlevel = 0.0
  var time = 0L

  def act = eventloop {

    case shot : Sedation => sedationlevel = shot.level
    case Sleep => {}
    case Kick => kickrules
    case Kill => killrules
    case TimeTick => {}
  }

  def createDreamlevel( name: String , mazeComplexity: Double = 0.5): DreamLevel =
        new DreamLevel( name, this, mazeComplexity )
  def realizeDreamlevel( dl : DreamLevel ): DreamLevel = {
    dl.realize(this, consciousness.top, trainingLevel)
  }

  /**
   * the Totem will indicate reality
   */
  def totemCheck: Boolean = {
    return !totem.isEmpty && ( consciousness.size == 1 ) && sane
  }

  def killrules = {

  }

  def kickrules = {

  }

  def ticktime = {
    time = time + 1
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