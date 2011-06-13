package org.dreamsim

import java.lang.{Long, Double}

case class Sleep( dream: DreamLevel )
case class Kick()
case class Kill()
case class Music()
case class TimeTick( increment: Long )
case class Sedation( level: Double = 0.5 )