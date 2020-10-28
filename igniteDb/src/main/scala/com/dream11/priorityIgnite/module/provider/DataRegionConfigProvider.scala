package com.dream11.priorityIgnite.module.provider

import com.dream11.priorityIgnite.module.IgniteConfig
import com.google.inject.{Inject, Provider}
import org.apache.ignite.configuration.DataRegionConfiguration

class DataRegionConfigProvider extends Provider[DataRegionConfiguration]{

  @Inject
  private var igniteConfig: IgniteConfig = _

  override def get(): DataRegionConfiguration = {
    val runTime: Runtime = Runtime.getRuntime
    val useHeapFraction = igniteConfig.getUseHeapFraction
    val totalMemory = runTime.totalMemory()

    val maxSize = (useHeapFraction*totalMemory).toLong

    val dataRegionConfiguration = new DataRegionConfiguration
    dataRegionConfiguration.setName("PrioritisationIgnite")
    dataRegionConfiguration.setMaxSize(maxSize)
    dataRegionConfiguration.setMetricsEnabled(true)

    dataRegionConfiguration
  }
}
