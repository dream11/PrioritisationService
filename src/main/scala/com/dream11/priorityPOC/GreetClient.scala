package com.dream11.priorityPOC

import com.example.simple.sample.{GreetRequest, GreetingGrpc}
import io.grpc.ManagedChannel
import io.grpc.netty.NettyChannelBuilder
import org.apache.ignite.Ignite
import org.apache.ignite.resources.IgniteInstanceResource

object GreetClient extends App {

  @IgniteInstanceResource var ignite: Ignite = _

  val channel: ManagedChannel =
    NettyChannelBuilder
      .forAddress("localhost", 50001)
      .defaultLoadBalancingPolicy("round-robin")
      .usePlaintext()
      .build()

  val greetClient = GreetingGrpc.blockingStub(channel)

  val greetingRequest = GreetRequest.apply("Duggal Saab")

  val greetingResponse = greetClient.greet(greetingRequest)
  println(greetingResponse.message)
}
