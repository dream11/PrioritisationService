package com.dream11.priorityPOC.client

import java.net.{InetAddress, InetSocketAddress}
import java.util

import com.example.simple.sample.{GreetRequest, GreetingGrpc}
import io.grpc.ManagedChannel
import io.grpc.netty.NettyChannelBuilder
import org.apache.ignite.Ignite
import org.apache.ignite.cluster.ClusterGroup

class GrpcClient {

  //this will be used to get service nodes in cluster where grpc request has to be sent.
  private var ignite: Ignite = _

  private var serviceNodes: ClusterGroup = _

  def init(ignite: Ignite): Unit = {
    this.ignite = ignite
    this.serviceNodes = ignite.cluster().forAttribute("ROLE", "service.node")
    run()
  }

  def getInetSocketAddress(str: String): Array[InetAddress] = {
    val inetAddress: Array[InetAddress] = InetAddress.getAllByName(str)
    util.Arrays.stream(inetAddress).forEach(inetAddress => println(inetAddress.getHostAddress))
    inetAddress
  }

  def run(): Unit = {
    val socketAddresses: Array[InetAddress] = getInetSocketAddress("prioritisation-test.dream11-stag.local")
    var i = 0
    var inetSocketAddresses : List[InetSocketAddress] = List.empty
    util.Arrays
      .stream(socketAddresses)
      .forEach(socketAddress => {
        inetSocketAddresses = inetSocketAddresses :+ new InetSocketAddress(socketAddress.getHostAddress, 50000+i)
        i += 1
      })
    inetSocketAddresses.foreach(println)
//    var serviceNodeAddresses : List[SocketAddress] = List.empty
//    serviceNodes
//      .nodes()
//      .stream()
//      .map(node => {
//        val hashset = node.asInstanceOf[TcpDiscoveryNode].socketAddresses().asInstanceOf[java.util.HashSet[InetSocketAddress]]
//        val list : java.util.List[InetSocketAddress] = new util.ArrayList[InetSocketAddress](hashset)
//        list.get(1)
//      })
//      .forEach(socketAddress => serviceNodeAddresses :+ socketAddress)
////    serviceNodes
////      .nodes()
////      .stream()
////      .flatMap(node => node.asInstanceOf[TcpDiscoveryNode].socketAddresses().stream())
////      .forEach(socketAddress => serviceNodeAddresses :+ socketAddress)
////
//    val list = List.empty :+ new InetSocketAddress("127.0.0.1", 50000) :+ new InetSocketAddress("127.0.0.1", 50001) :+ new InetSocketAddress("127.0.0.1", 50002)
    val channel : ManagedChannel =
      NettyChannelBuilder
        .forTarget("service")
        .nameResolverFactory(new MultiAddressNameResolverFactory(inetSocketAddresses))
        .defaultLoadBalancingPolicy("round_robin")
        .usePlaintext()
        .build()

    val greetClient = GreetingGrpc.blockingStub(channel)

    var j = 0
    while (j < 8) {
      val greetingRequest = GreetRequest.apply("Duggal Saab")

      val greetingResponse = greetClient.greet(greetingRequest)
      println(greetingResponse.message)
      j = j+1
    }
  }
}
