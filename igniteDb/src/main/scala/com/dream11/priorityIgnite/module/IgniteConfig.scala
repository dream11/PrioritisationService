package com.dream11.priorityIgnite.module

import scala.beans.BeanProperty

class IgniteConfig {
  @BeanProperty
  var ipAddresses: String = _

  @BeanProperty
  var useHeapFraction: Double = _

  @BeanProperty
  var localPort: Int = _

  @BeanProperty
  var portRange: Int = _
}
