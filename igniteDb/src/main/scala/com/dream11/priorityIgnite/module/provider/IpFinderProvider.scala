package com.dream11.priorityIgnite.module.provider

import java.util

import com.dream11.priorityIgnite.module.IgniteConfig
import com.google.inject.{Inject, Provider}
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder

class IpFinderProvider extends Provider[TcpDiscoveryVmIpFinder] {

  @Inject
  private var igniteConfig: IgniteConfig = _

  override def get(): TcpDiscoveryVmIpFinder = {
    val ipFinder = new TcpDiscoveryVmIpFinder
    ipFinder.setAddresses(util.Arrays.asList(igniteConfig.getIpAddresses()))
    ipFinder
  }
}
