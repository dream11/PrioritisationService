package com.dream11.priorityIgnite

import com.dream11.priorityIgnite.module.IgniteModule
import com.dream11.priorityIgnite.util.Constants
import com.google.inject.{Guice, Injector}
import com.typesafe.scalalogging.LazyLogging
import org.apache.ignite.configuration.{DataRegionConfiguration, DataStorageConfiguration, IgniteConfiguration}
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.{Ignite, Ignition}

object Application extends LazyLogging {

  val cacheConfigProvider = new CacheConfigProvider
  val injector: Injector = Guice.createInjector(new IgniteModule)

  def main(args: Array[String]): Unit = {
    //initiate cache config provider
    cacheConfigProvider.init(injector)

    //get tcp discovery spi
    val tcpDiscoverySpi: TcpDiscoverySpi = injector.getInstance(classOf[TcpDiscoverySpi])

    //set default data region config
    val dataStorageConfiguration: DataStorageConfiguration = new DataStorageConfiguration
    val dataRegionConfig : DataRegionConfiguration = injector.getInstance(classOf[DataRegionConfiguration])
    dataStorageConfiguration.setDefaultDataRegionConfiguration(dataRegionConfig)

    //initiate tables
    val criteriaWeightsCacheConfig = cacheConfigProvider.getCacheConfig[String, Double](Constants.CRITERIA_WEIGHTS)

    //finally initialise igniteConfiguration
    val igniteConfiguration: IgniteConfiguration = new IgniteConfiguration
    igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration)
    igniteConfiguration.setCacheConfiguration(criteriaWeightsCacheConfig)
    igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi)

    val ignite: Ignite = Ignition.start(igniteConfiguration)
  }
}

