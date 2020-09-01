package com.dream11.priorityPOC.serviceNode

import com.dream11.protoc.transaction.{JobType, PriorityResponse, Source, Transaction}
import com.dream11.protoc.transaction.TransactionPrioritisationGrpc.TransactionPrioritisation
import org.apache.ignite.cache.query.{QueryCursor, SqlFieldsQuery}
import org.apache.ignite.{Ignite, IgniteCache}
import org.apache.ignite.resources.IgniteInstanceResource

import scala.concurrent.Future

class DefaultPrioritySerivce extends TransactionPrioritisation{

  @IgniteInstanceResource var ignite: Ignite = _
  private val criteriaValues: IgniteCache[String, Double] = ignite.cache("criteriaValues")
  private val criteriaWeights: IgniteCache[String, Double] = ignite.cache("criteriaWeights")
  private val priorityLevels: IgniteCache[Int, Transaction] = ignite.cache("priorityLevels")

  override def prioritise(request: Transaction): Future[PriorityResponse] = {
    val jobType = request.jobType
    val source = request.source
    val ttl = request.executionGuaranteed
    val absolutePriorityWeight = (getJobTypeNormalizedWeight(jobType) + getSourceNormalizedWeight(source) + getTtlNormalizedWeight(ttl))/3.0
    val cursor: QueryCursor[List[_]] = priorityLevels.query(new SqlFieldsQuery(s"select * from priorityLevels where lowerBound<$absolutePriorityWeight && upperBound>=$absolutePriorityWeight"))
    val row = cursor.iterator().next()
    row.foreach(println)
    Future.successful(new PriorityResponse())
  }

  private def getJobTypeNormalizedWeight(jobType: JobType): Double = {
    val criteriaValueKey = "JOB_TYPE_" + jobType
    val criteriaValue = criteriaValues.get(criteriaValueKey)
    val criteriaWeight = criteriaWeights.get("JOB_TYPE")
    criteriaWeight * (100-criteriaValue)/100
  }

  private def getSourceNormalizedWeight(source: Source): Double = {
    val sourceKey = "SOURCE_" + source
    val criteriaValue = criteriaValues.get(sourceKey)
    val sourceWeight = criteriaWeights.get("SOURCE")
    criteriaValue * (100 - sourceWeight.doubleValue())/100
  }

  private def getTtlNormalizedWeight(ttl: Int): Double = {
    (86400.0 - ttl.doubleValue())/ 86400.0
  }
}
