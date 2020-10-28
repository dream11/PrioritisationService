package com.dream11.priorityIgnite

import com.dream11.priorityIgnite.module.cacheConfig.annotations.CriteriaWeightsCacheAnnotation
import com.google.inject.{Injector, Key}
import org.apache.ignite.configuration.CacheConfiguration

class CacheConfigProvider {
  private var injector: Injector = _

  def init(injector: Injector): Unit = {
    this.injector = injector
  }

  def getCacheConfig[T,U](cacheName: String): CacheConfiguration[T,U] = {
    cacheName match {
      case "criteriaWeights" => injector.getInstance(Key.get(classOf[CacheConfiguration[T, U]], classOf[CriteriaWeightsCacheAnnotation]))
    }
  }
}
