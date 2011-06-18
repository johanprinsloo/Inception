package org.dreamsim

import org.scalatest.FunSuite
import grizzled.slf4j.Logging
import actors.Actor

class CharacterTest extends FunSuite with Logging {

  test("Character Test"){
    val buster = Character("Buster")
    val jamie = Character("Jamie")
    val adam = Character("Adam")

    assert( buster.consciousness.size == 1,
      " buster consciousness stack size " + buster.consciousness.size)
    assert( buster.consciousness.top == Scenario.reality,
      " buster reality " + buster.consciousness.top )

    val dream = adam createDreamlevel "dream"
    assert( dream.creator == adam )
    assert(adam.consciousness.size == 1 )
    assert(adam.consciousness.top === Scenario.reality)
  }

  test("Character in Group Test"){
    val buster = Character("Buster")
    val jamie = Character("Jamie")
    val adam = Character("Adam")
    info( "Actor state 1 " + buster.getState )

    val testers = Group("Testers") <<~ buster <<~ jamie <<~ adam
    assert( testers.members.size == 3  )
    assert( testers.members.contains(buster) )
    assert( testers.members.contains(jamie) )
    assert( testers.members.contains(adam) )

    info("unknown message test")
    testers !! "an unknown message"

    (1 to 10).par.foreach { m => testers ! m}
    assert( buster.getState == Actor.State.Runnable )
    info( "Actor state 2 " + buster.getState )

    info("time increment test")
    testers !! TimeTick( 100 )
    Thread.sleep(500)

    assert( buster.time === 100 )
    assert( jamie.time === 100 )
    assert( adam.time === 100 )

    info( "Actor state 3 " + buster.getState )
    assert( buster.getState == Actor.State.Suspended | buster.getState == Actor.State.Runnable )

    testers !! "exit"

    Thread.sleep(500)
    info( "Actor state 4 " + buster.getState )
    assert( buster.getState == Actor.State.Terminated )

  }
}