package com.dream11.priorityIgnite.module.cacheConfig

import org.apache.ignite.configuration.CacheConfiguration

trait CacheConfig[T, U] {
  def getConfig(): CacheConfiguration[T, U]
}
