package com.dream11.priorityIgnite.module.cacheConfig.impl

import com.dream11.priorityIgnite.module.cacheConfig.CacheConfig
import org.apache.ignite.cache.CacheMode
import org.apache.ignite.configuration.CacheConfiguration

object CriteriaWeightsCacheConfiguration extends CacheConfig[String, Double]{

  override def getConfig: CacheConfiguration[String, Double] = {
    val cacheConfig = new CacheConfiguration[String, Double]("criteriaWeights")
    cacheConfig.setCacheMode(CacheMode.REPLICATED)
    cacheConfig
  }
}
