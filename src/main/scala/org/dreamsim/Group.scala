package org.dreamsim

import actors.Actor
import actors.Actor._
import java.util.jar.Attributes.Name

/**
 * A Group is a group of Characters including the team and the subject
 */
class Group(name:  String ) extends Actor {

  var members: Set[Character] = Set.empty

  def act = eventloop {
    case "disband" => { members.empty; exit() }
    case _ => { members foreach { member => member ! _ } }
  }

  def ~>> ( newmember: Character) : Group = {
    members += newmember
    return this
  }
}

object Group {
 def apply( name: String) : Group = new Group(name)
}