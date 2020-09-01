package com.dream11.priorityPOC.dataNode

import java.util.Arrays
import java.util.concurrent.ConcurrentHashMap

import com.dream11.priorityPOC.pojo.{PriorityLevels, Transaction}
import org.apache.ignite.Ignition
import org.apache.ignite.cache.{CacheAtomicityMode, CacheMode}
import org.apache.ignite.configuration.{CacheConfiguration, IgniteConfiguration}
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

object DataNode extends App {
  val igniteConfiguration = new IgniteConfiguration

  val discoverySpi = new TcpDiscoverySpi
  discoverySpi.setLocalPort(48500)
  discoverySpi.setLocalPortRange(20)

  val ipFinder = new TcpDiscoveryVmIpFinder
  ipFinder.setAddresses(Arrays.asList("127.0.0.1:48500..48520"))


  discoverySpi.setIpFinder(ipFinder)

  //criteria weights table
  val criteriaWeightsConfig = new CacheConfiguration[String, Double]("criteriaWeights")
  criteriaWeightsConfig.setCacheMode(CacheMode.REPLICATED)
  criteriaWeightsConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC)

  //store transaction with prioritised value
  val transactionWeightConfig = new CacheConfiguration[String, Transaction]("transaction")
  transactionWeightConfig.setCacheMode(CacheMode.PARTITIONED)
  criteriaWeightsConfig.setBackups(1)
  criteriaWeightsConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC)

  //store criteria values mapped to a quantifiable value
  val criteriaValueConfig = new CacheConfiguration[String,Int]("criteriaValues")
  criteriaValueConfig.setCacheMode(CacheMode.REPLICATED)
  criteriaWeightsConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC)

  //store list of existing criteria which can be changed dynamically
  val criteriaListConfig = new CacheConfiguration[String, List[String]]("criteriaList")
  criteriaListConfig.setCacheMode(CacheMode.REPLICATED)
  criteriaListConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC)

  val priorityLevelConfig = new CacheConfiguration[Int, PriorityLevels]("priorityLevels")
  priorityLevelConfig.setCacheMode(CacheMode.REPLICATED)
  priorityLevelConfig.setAtomicityMode(CacheAtomicityMode.ATOMIC)

  igniteConfiguration.setDiscoverySpi(discoverySpi)
  igniteConfiguration.setCacheConfiguration(criteriaWeightsConfig, criteriaValueConfig, criteriaListConfig, transactionWeightConfig, priorityLevelConfig)

  val userAttributes = new ConcurrentHashMap[String, String]()
  userAttributes.put("ROLE", "data.node")
  igniteConfiguration.setUserAttributes(userAttributes)

  igniteConfiguration.setPeerClassLoadingEnabled(true)

  val ignite = Ignition.start(igniteConfiguration)

  val dataInitialisation = new DataInitialisation(ignite)
  dataInitialisation.init()

//  println("Ignite config has been started on " + ignite.cluster().nodes())
//
//  val collection = ignite.cluster().nodes()
//  collection.stream().map(node => node.asInstanceOf[TcpDiscoveryNode].discoveryPort()).forEach(println)
}
