package com.dream11.priorityPOC.serviceNode

import java.util.Arrays
import java.util.concurrent.ConcurrentHashMap

import org.apache.ignite.Ignition
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.services.ServiceConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

object ServiceNode extends App {
  val igniteConfiguration = new IgniteConfiguration

  val discoverySpi = new TcpDiscoverySpi
  discoverySpi.setLocalPort(48500)
  discoverySpi.setLocalPortRange(20)

  val ipFinder = new TcpDiscoveryVmIpFinder
  ipFinder.setAddresses(Arrays.asList("127.0.0.1:48500..48520"))

  discoverySpi.setIpFinder(ipFinder)

  val serviceConfig : ServiceConfiguration = new ServiceConfiguration
  serviceConfig.setService(new PrioritisationService)
  serviceConfig.setName("prioritisationService")
  serviceConfig.setMaxPerNodeCount(1)


  igniteConfiguration.setDiscoverySpi(discoverySpi)

  val userAttributes = new ConcurrentHashMap[String, String]()
  userAttributes.put("ROLE", "service.node")
  igniteConfiguration.setUserAttributes(userAttributes)

  igniteConfiguration.setPeerClassLoadingEnabled(true)

  val ignite = Ignition.start(igniteConfiguration)

  ignite.services().deploy(serviceConfig)
}
