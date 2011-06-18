package org.dreamsim

import grizzled.slf4j.Logging
import org.scalatest.FunSuite
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing.Validation
import sun.tools.tree.AssignRemainderExpression
import sun.applet.AppletSecurityException

class DreamTest extends FunSuite with Logging{

  test("DreamTest basics") {
    val buster = Character("Buster")
    val jamie = Character("Jamie")
    val adam = Character("Adam")

    val dreamgroup1 = Group("dreamgroup1") <<~ adam <<~ jamie <<~ buster

    val dreamlevel1 = adam createDreamlevel "dream1"
    assert( dreamlevel1.creator == adam )

    val dream2 = adam createDreamlevel "dream2"
    assert( dream2.creator == adam )

    info("Test group dream")

    assert( Scenario.reality.characters.contains(buster),
      " characters contained in reality " + Scenario.reality.characters )
    assert( Scenario.reality.characters.contains(adam), " characters contained in reality" )
    assert( Scenario.reality.characters.contains(jamie), " characters contained in reality" )



    Scenario.reality !! TimeTick( 10 )
    Thread.sleep(100)
    assert( Scenario.reality.time == 10 , "reality time inc")

    assert( buster.time == Scenario.reality.time * Scenario.levelTimeMultiplier,
      " character time multiplier: \t charater time: " + buster.time +
      " reality time: " + Scenario.reality.time )

    val dream1 = buster realizeDreamlevel  dreamlevel1

    assert( Scenario.reality.nextlevel == dream1 , "next level linked correctly ")
    assert( dream1.previouslevel == Scenario.reality , "previous level linked correctly ")
    dreamgroup1 !! Sleep ( dream1 )
    Thread.sleep(100)
    assert( dream1.characters.contains(buster), "dream1 contains buster" )
    assert(dream1.characters.contains(jamie))
    assert(dream1.characters.contains(adam))
    assert(dream1.characters.contains(adam))

    Scenario.reality !! TimeTick( 59 )
    Thread.sleep(100)

    assert( buster.time == Scenario.reality.time * Scenario.levelTimeMultiplier, "character time multiplier" )

    dreamgroup1 !! "exit"
    dream1 !! "exit"
    dream2 !! "exit"

  }
}