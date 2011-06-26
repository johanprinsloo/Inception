package org.dreamsim

import grizzled.slf4j.Logging
import org.scalatest.FunSuite
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing.Validation
import sun.tools.tree.AssignRemainderExpression
import sun.applet.AppletSecurityException
import org.dreamsim.TimeTick

class DreamTest extends FunSuite with Logging{

  test("DreamTest basics") {
    val buster = Character("Buster")
    val jamie = Character("Jamie")
    val adam = Character("Adam")

    val dreamgroup1 = Group("dreamgroup1") <<~ adam <<~ jamie <<~ buster

    val dreamlevel1 = adam createDreamlevel "dream1"
    assert( dreamlevel1.creator == adam )

    val dreamlevel2 = adam createDreamlevel "dream2"
    assert( dreamlevel2.creator == adam )

    info("Test group dream")

    assert( Scenario.reality.characters.contains(buster),
      " characters contained in reality " + Scenario.reality.characters )
    assert( Scenario.reality.characters.contains(adam), " characters contained in reality" )
    assert( Scenario.reality.characters.contains(jamie), " characters contained in reality" )

    Scenario.reality !! TimeTick( 10 )
    Thread.sleep(100)
    assert( Scenario.reality.time == 10 , "reality time inc")

    assert( buster.time === Scenario.reality.time,
      " character time multiplier: \t charater time: " + buster.time +
      " reality time: " + Scenario.reality.time )

    val dream1 = buster realizeDreamlevel  dreamlevel1

    assert( Scenario.reality.nextlevel.get == dream1 , "next level linked correctly ")
    assert( dream1.previouslevel.get == Scenario.reality , "previous level linked correctly ")

    dreamgroup1 !! Sleep ( dream1 )
    Thread.sleep(100)
    assert( dream1.characters.contains(buster), "dream1 contains buster" )
    assert(dream1.characters.contains(jamie))
    assert(dream1.characters.contains(adam))

    Scenario.reality !! TimeTick( 59 )
    Thread.sleep(100)

    assert( buster.time === 10 + 59 * Scenario.levelTimeMultiplier, "character time multiplier" )

    val dreamgroup2 = Group("dreamgroup2") <<~ adam <<~ jamie
    val dream2 = adam realizeDreamlevel dreamlevel2
    assert(dream1.nextlevel.get == dream2, "dream 1 uplinked")
    assert(dream2.previouslevel.get == dream1, "dream 2 back linked")
    dreamgroup2 !! Sleep( dream2 )
    Thread.sleep(100)
    assert(dream2.characters.contains(jamie))
    assert(dream2.characters.contains(adam))
    assert(jamie.consciousness.top === dream2)

    Scenario.reality !! TimeTick( 10 )
    Thread.sleep(100)
    assert(adam.time === 10 + (59 * Scenario.levelTimeMultiplier) +
      (10 * Scenario.levelTimeMultiplier * Scenario.levelTimeMultiplier) , " second level character time multiplier")

    jamie !! Kick
    Thread.sleep(100)
    assert(jamie.consciousness.top === dream1)
    assert(!dream2.characters.contains(jamie))

    adam !! Kill
    Thread.sleep(100)
    assert(adam.consciousness.top === dream1)
    assert(!dream2.characters.contains(adam))

    adam !! Sedation(0.8)
    Thread.sleep(100)
    adam !! Kill
    Thread.sleep(100)
    assert(adam.consciousness.top === Scenario.limbo)

    dreamgroup1 !! "exit"
    dream1 !! "exit"
    dream2 !! "exit"

  }
}