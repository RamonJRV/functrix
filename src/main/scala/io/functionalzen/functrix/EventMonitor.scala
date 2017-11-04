package io.functionalzen.functrix

import io.functionalzen.functrix.event.Event

trait EventMonitor {

  type MonitorName = String

  val monitorName : MonitorName

  def update(event : Event) : Unit

}//end trait EventMonitor
