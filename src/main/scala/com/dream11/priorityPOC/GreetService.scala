package com.dream11.priorityPOC

import com.example.simple.sample.{GreetRequest, GreetResponse, GreetingGrpc}
import org.apache.ignite.Ignite
import org.apache.ignite.spi.discovery.tcp.internal.TcpDiscoveryNode

import scala.concurrent.Future

class GreetService(val ignite: Ignite, val portNumber: Int) extends GreetingGrpc.Greeting {
  override def greet(request: GreetRequest): Future[GreetResponse] = {
    val reply = GreetResponse.apply(message = "Mr " + request.message + ". Response is being sent from port number: " + portNumber)
    val serviceNumber = ignite.cluster().localNode().asInstanceOf[TcpDiscoveryNode].discoveryPort()%10
    println("Service to receive request is " + serviceNumber)
    Future.successful(reply)
  }
}
