package com.dream11.priorityPOC.client

import java.util.Arrays

import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder
import org.apache.ignite.{Ignite, Ignition}

object IgniteClient extends App {

  val igniteConfiguration = new IgniteConfiguration

  val discoverySpi = new TcpDiscoverySpi
  discoverySpi.setLocalPort(48500)
  discoverySpi.setLocalPortRange(20)

  val ipFinder = new TcpDiscoveryVmIpFinder
  ipFinder.setAddresses(Arrays.asList("127.0.0.1:48500..48520"))

  discoverySpi.setIpFinder(ipFinder)
  igniteConfiguration.setClientMode(true)
  igniteConfiguration.setDiscoverySpi(discoverySpi)
  igniteConfiguration.setPeerClassLoadingEnabled(true)

  val ignite: Ignite = Ignition.start(igniteConfiguration)

  val grpcClient: GrpcClient = new GrpcClient
  grpcClient.init(ignite)
}
