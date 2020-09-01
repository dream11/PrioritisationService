package com.dream11.priorityPOC.client

import java.net.{SocketAddress, URI}

import io.grpc.NameResolver.ResolutionResult
import io.grpc.{Attributes, EquivalentAddressGroup, NameResolver}

import scala.jdk.CollectionConverters._

class MultiAddressNameResolverFactory(socketAddresses: List[SocketAddress]) extends NameResolver.Factory{

  override def newNameResolver(targetUri: URI, args: NameResolver.Args): NameResolver = {
    new NameResolver {
      override def getServiceAuthority: String = {
        "fakeAuthority"
      }

      override def start(listener: NameResolver.Listener2): Unit = {
        var addresses: List[EquivalentAddressGroup] = List.empty
        for (socketAddress <- socketAddresses) {
          addresses = addresses :+ new EquivalentAddressGroup(socketAddress)
        }
        println(addresses)
        val addressesAsJavaList: java.util.List[EquivalentAddressGroup] = addresses.asJava
        println(addressesAsJavaList)
        listener.onResult(ResolutionResult.newBuilder().setAddresses(addressesAsJavaList).setAttributes(Attributes.EMPTY).build())
      }

      override def shutdown(): Unit = {
      }
    }
  }

  override def getDefaultScheme: String = {
    "multiAddress"
  }
}
