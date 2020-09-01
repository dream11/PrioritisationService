package com.dream11.priorityPOC.dataNode

import breeze.linalg.{DenseMatrix, DenseVector}
import com.dream11.priorityPOC.pojo.PriorityLevels
import org.apache.ignite.{Ignite, IgniteCache}

class DataInitialisation(val ignite: Ignite) {

  def init(): Unit = {
    storeCriteriaWeights()
    storeCriteriaValues()
    storePriorityLevelConfig()
  }

  def storeCriteriaWeights(): Unit = {
    //comparison matrix representing weights of criteria with respect to each other.
    //TODO Get this comparison matrix from db.
    val comparisonMatrix: DenseMatrix[Double] = DenseMatrix((1.0,8.0,2.0),(0.125,1.0,0.25),(0.5,4.0,1.0))

    //TODO check consistency of matrix.
    //normalized matrix
    for (i <- 0 until comparisonMatrix.cols) {
      val vector: DenseVector[Double] = comparisonMatrix(::, i)
      var sum = 0.0
      for (j <- 0 until vector.length) {
        sum = sum + vector.valueAt(j)
      }
      for (j <- 0 until vector.length) {
        vector.update(j, vector.valueAt(j)/sum)
      }
    }
    //weighted normalization.
    //TODO use length of criteria list from local cache instead of comparisonMatrix.rows
    var weightedCriteria: List[Double] = List.empty
    for (k <- 0 until comparisonMatrix.rows) {
      val vector = comparisonMatrix(k, ::)
      var sum = 0.0
      for (j <- 0 until vector.inner.length) {
        sum = sum + vector.inner.valueAt(j)
      }
      weightedCriteria = weightedCriteria :+ sum/comparisonMatrix.rows
    }

    val criteriaWeights: IgniteCache[String, Double] = ignite.cache("criteriaWeights")
    criteriaWeights.put("JOB_TYPE", weightedCriteria.toArray.apply(0))
    criteriaWeights.put("SOURCE", weightedCriteria.toArray.apply(1))
    criteriaWeights.put("EXECUTION_GUARANTEED", weightedCriteria.toArray.apply(2))
    println(criteriaWeights.get("JOB_TYPE"))
  }

  def storeCriteriaValues(): Unit = {
    //TODO Think of a way to add criteria values such that it is forward compatible with new transaction types
    val criteriaValues: IgniteCache[String, Int] = ignite.cache("criteriaValues")
    criteriaValues.put("JOB_TYPE_WINNER_DECLARATION", 1)
    criteriaValues.put("JOB_TYPE_REFUND", 4)
    criteriaValues.put("EXECUTION_GUARANTEED", 86400)
    criteriaValues.put("SOURCE_CONTEST_JOIN", 1)
    criteriaValues.put("SOURCE_FPV", 2)
  }

  def storePriorityLevelConfig(): Unit = {
    val priorityLevels: IgniteCache[Int, PriorityLevels] = ignite.cache("priorityLevels")
    //PL1
    val pl1 = PriorityLevels.builder().priorityLevel(1).lowerBound(0.8).upperBound(1).build()
    val pl2 = PriorityLevels.builder().priorityLevel(2).lowerBound(0.6).upperBound(0.8).build()
    val pl3 = PriorityLevels.builder().priorityLevel(3).lowerBound(0.4).upperBound(0.6).build()
    val pl4 = PriorityLevels.builder().priorityLevel(4).lowerBound(0.2).upperBound(0.4).build()
    val pl5 = PriorityLevels.builder().priorityLevel(5).lowerBound(0.0).upperBound(0.2).build()
    priorityLevels.put(1, pl1)
    priorityLevels.put(2, pl2)
    priorityLevels.put(3, pl3)
    priorityLevels.put(4, pl4)
    priorityLevels.put(5, pl5)
  }
}
