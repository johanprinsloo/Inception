package org.dreamsim

import actors.Actor
import actors.Actor._
import grizzled.slf4j._

/**
 * A Group is a group of Characters including the team and the subject
 */
class Group( val name: String ) extends Actor with Logging {

  var members: Set[Character] = Set.empty

  def act = eventloop {
    case "disband" => { members.empty; exit() }
    case "exit" => { members foreach { member => member ! "exit" }; exit() }
    case m => { members foreach { member => member !? m }; reply() }
  }

  def <<~ ( newmember: Character) : Group = {
    info("Adding " + newmember.name + " to " + name )
    members += newmember
    return this
  }

  def ~>> ( kickmember: Character) : Group = {
    info("Kicking  " + kickmember.name + " from " + name + " group")
    members -= kickmember
    return this
  }
  
}

object Group {
 def apply( name: String) : Group = {
   val gr = new Group(name)
   gr.start
   return gr
 }
}