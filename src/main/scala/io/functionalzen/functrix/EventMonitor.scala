package io.functionalzen.functrix

import io.functionalzen.functrix.event.Event

object EventMonitor {
  type MonitorName = String

}

trait EventMonitor {

  val monitorName : EventMonitor.MonitorName

  def update(event : Event) : Unit

}//end trait EventMonitor
