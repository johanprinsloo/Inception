package org.dreamsim

import scala.actors._
import Actor._
import collection.mutable.Stack
import grizzled.slf4j._

class Totem(name : String ) {
  var state = true
}

class Character( val name: String,
                 val totem: Option[Totem] ) extends Actor with Logging {

  Scenario.getSceneTime
  var sane = true
  var psychosis  = 0.0D
  var consciousness = Stack.empty[DreamLevel]
  var trainingLevel: Double = 0.5
  var sedationlevel: Double = 0.0
  var time = 0L
  info( name + " created ")

  def act = eventloop {
    case shot : Sedation => sedationlevel = shot.level
    case sleep: Sleep => sleeprules( sleep.dream )
    case Kick => kickrules
    case Kill => killrules
    case tt : TimeTick => timeIncrement( tt )
    case "exit" => {info( name + " is done"); exit()}
    case m => info( name + " gets unknown message " + m )
  }

  def createDreamlevel( name: String , mazeComplexity: Double = 0.5): DreamLevel = {
    DreamLevel( name, this, mazeComplexity )
  }

  def realizeDreamlevel( dl : DreamLevel ): DreamLevel = {
    dl.realize(this, consciousness.top, trainingLevel)
  }

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
        Scenario.limbo <-- this
        consciousness push Scenario.limbo
        info( name + " killed and dropped to limbo")
      }
      case d:Double if d < Scenario.criticalSedationLevel => { // kick up a level
        (consciousness.pop) --> ( this )
        info( name + " killed up to " + (consciousness.top).name )
      }
    }
  }

  def kickrules = {
    (consciousness.pop) --> ( this )
    info( name + " kicked up to " + (consciousness.top).name )
  }

  def timeIncrement(tick: TimeTick): Unit = {
     time = time + tick.increment
     info( name + " dream time advanced by " + tick.increment + " to " + time )
  }

  def getProjections( mazeComplexity: Double ) : Set[Projection] = {
    Set.empty
  }

  def getPsychoticProjections( mazeComplexity: Double ) : Set[Projection] = {
    psychosis match {
      case ps:Double if ps > 0.0 => Set.empty
      case _ => Set.empty
    }
    Set.empty
  }

  //add charater to the dream
  def --> ( dream: DreamLevel ) : Character = {
    consciousness push dream
    dream <-- this
    info( name + " added to " + dream.name + " during construction " + consciousness.size)
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

object Character extends Logging {

  private def makeChar(ch: Character): Character = {
    ch.start
    ch --> Scenario.reality
  }

  def apply ( name: String, totem: Option[Totem] ):  Character = {
    makeChar(new Character( name, totem ))
  }

  def apply ( name: String ): Character =  {
    makeChar( new Character( name, None ) )
  }

  def apply ( ): Character = {
    makeChar( new Character("Jane", None) )
  }

  def totembyname( name: String, totemname: Option[String] ): Character = {
    val totem: Option[ Totem ] = totemname match {
      case None => None
      case Some(name) => Option( new Totem(name) )
    }
    makeChar( new Character(name, totem ) )
  }

  def scenetime : Long = Scenario getSceneTime
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