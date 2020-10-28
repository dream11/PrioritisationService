package com.dream11.priorityIgnite.module.provider

import com.dream11.priorityIgnite.module.IgniteConfig
import com.google.inject.{Inject, Provider}
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

class TcpDiscoverySpiProvider extends Provider[TcpDiscoverySpi]{

  @Inject
  var igniteConfig: IgniteConfig = _

  @Inject
  var ipFinder:TcpDiscoveryVmIpFinder = _

  override def get(): TcpDiscoverySpi = {
    val tcpDiscoverySpi: TcpDiscoverySpi = new TcpDiscoverySpi
    tcpDiscoverySpi.setLocalPort(igniteConfig.getLocalPort)
    tcpDiscoverySpi.setLocalPortRange(igniteConfig.getPortRange)
    tcpDiscoverySpi.setIpFinder(ipFinder)
    tcpDiscoverySpi
  }
}
