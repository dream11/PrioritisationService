package com.dream11.priorityPOC.serviceNode

import com.dream11.protoc.transaction.TransactionPrioritisationGrpc
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder
import org.apache.ignite.Ignite
import org.apache.ignite.resources.IgniteInstanceResource
import org.apache.ignite.services.{Service, ServiceContext}

import scala.concurrent.ExecutionContext

class PrioritisationService extends Service{

  println("Started Service")
  private var server: Server = _
  @IgniteInstanceResource var ignite: Ignite = _

  override def cancel(ctx: ServiceContext): Unit = {
    if (server != null)
      server.shutdown()
  }

  override def init(ctx: ServiceContext): Unit = {
    println("Starting Prioritisation Service inside ignite cluster")
  }

  //this will create grpc server
  override def execute(ctx: ServiceContext): Unit = {

    println(ignite.cluster().nodes())
    var i = 0;
    while (i<3) {
      server = NettyServerBuilder.forPort(50000 + i).addService(TransactionPrioritisationGrpc.bindService(new DefaultPrioritySerivce(), ExecutionContext.global)).build.start
      println("Grpc Server started at port " + (50000+i))
      i+=1
    }

    sys.addShutdownHook {
      System.err.println("Shutting down gRPC server since JVM is shutting down")
      server.shutdown()
      System.err.println("Server shut down")
    }

    server.awaitTermination()
  }
}
