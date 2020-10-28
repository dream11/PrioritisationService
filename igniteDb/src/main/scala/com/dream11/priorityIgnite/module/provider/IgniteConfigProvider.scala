package com.dream11.priorityIgnite.module.provider

import com.dream11.common.util.ConfigProvider
import com.dream11.priorityIgnite.module.IgniteConfig
import com.google.inject.{Inject, Provider}
import com.typesafe.config.{Config, ConfigBeanFactory, ConfigFactory}

class IgniteConfigProvider extends Provider[IgniteConfig]{

  @Inject
  private var configProvider: ConfigProvider = _

  override def get(): IgniteConfig = {
    val env: String = java.util.Optional.ofNullable(System.getProperty("app.environment")).orElse("prod")
    val configFile: String = "config/ignite-" + env + ".conf"
    val igniteConfig: Config = ConfigFactory.load(configFile).withFallback(ConfigFactory.load("config/ignite-default.conf"))
    ConfigBeanFactory.create(igniteConfig, classOf[IgniteConfig])
  }
}
