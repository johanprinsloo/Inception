package org.dreamsim

class InceptionScenario {
  val saito = Character("Saito", None ) //we don't know if Saito has a Totem
  val cobb = Character("Cobb", Some( new Totem("spinner")) )
  val arthur = Character("Arthur", Some( new Totem("dice")) )
  val ariadne = Character("Ariadne", Some( new Totem("rook")) )
  val eames = Character("Eames", None )
  val yusuf = Character("Yusuf", None )
  val fisher = Character("Robert Fisher(the Subject)", None )

  val city = ariadne createDreamlevel "city"
  val hotel = ariadne createDreamlevel "hotel"
  val icefortress = ariadne createDreamlevel "icefortress"

  val citygroup = Group("city") <<~ saito <<~ cobb <<~ arthur <<~ ariadne <<~ eames <<~ yusuf <<~ fisher
  citygroup ! Sedation(0.9)
  val citydream = yusuf.realizeDreamlevel(city)
  citygroup ! Sleep( citydream )
  citydream ! TimeTick( 1000 )  //they get in trouble here - decide to play a Mr Smith??

  val hotelgroup = Group("hotel") <<~  saito <<~ cobb <<~ arthur <<~ ariadne <<~ eames <<~ fisher
  val hoteldream = arthur realizeDreamlevel hotel
  hotelgroup ! Sleep( hoteldream )
  citydream ! TimeTick( 1000 )      // the time ticks through to the lower levels in a accelerated fashion

  val icefortressgroup = Group("ice") <<~ saito <<~ cobb <<~ ariadne <<~ eames <<~ fisher
  val icedream = eames realizeDreamlevel icefortress
  icefortressgroup ! Sleep( icedream )
  citydream ! TimeTick(1000)

  fisher ! Kill  //mal kills fisher
  citydream ! TimeTick(10)
  //cobb and ariadne decides to follow
  val limbo = Scenario.limbo
  val limbogroup = Group("limbo") <<~ cobb <<~ ariadne
  limbogroup ! Sleep( limbo )
  citydream ! TimeTick(10)

  saito ! Kill   //saito dies and drops into limbo
  citydream ! TimeTick(10)  //this translates to many years for saito

  //ardiane kills fisher and herself in limbo - they kick up to ice level
  fisher ! Kill
  ariadne ! Kill

  //arthur kicks the icefortress group
  icefortressgroup ! Kick

  //yusuf kicks the hotelgroup they wake in the submerged van
  hotelgroup ! Kick

  //cobb and Saito is still stuck in limbo
  cobb ! Kill
  saito ! Kill

  //everyone in the citydream
  citygroup ! Kick

  println( "How did this movie end anyway: " +
    "\n\t Did Cobb end up in reality? " + cobb.totemCheck )

  // the result should be statistically indeterminate, which leads to a exception and server crash

}