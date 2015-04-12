package learn.scala

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.collection.mutable.MutableList
import org.apache.spark.rdd.RDD

/**
 * Logistic regression application. The code is
 * adapted from the Java version from
 * <a href="https://github.com/tpeng/logistic-regression">
 * logistic-regression.
 * </a>
 */
object LogisticRegressionApp {
  
  class LRModel(_weights:List[Double]) extends Serializable {
    def weights = _weights
    
    def classify(x:Array[Double]): Double = {
      LogisticRegressionApp.classify(x, weights)
    }
  }
  
  class RWeights(_weights:List[Double]) extends Serializable {
    def weights = _weights
    
    def add(that:RWeights):RWeights = { 
      var weights:List[Double] = List()
      for(i <- 0 until this.weights.length) {
        val w = this.weights(i) + that.weights(i)
        weights = weights :+ w
      }
      new RWeights(weights)
    }
    
    def +(that:RWeights): RWeights = add(that)
  }
  
  def main(args:Array[String]) {
    var maxIterations = 100
    var rate = 0.0001d
    
    val trInput = args(0)
    val teInput = args(1)
    val output = args(2)
    
    try {
      maxIterations = args(3).toInt
      if(maxIterations < 100)
        maxIterations = 100
    }
    
    try {
      rate = args(4).toDouble
      if(rate < 0.0d) {
        rate = 0.0001d
      }
    }
    
    val sc = new SparkContext(
        new SparkConf()
        	.setAppName(s"Logistic regression $trInput $teInput $output")
        )
    
    val model = learnModel(sc, trInput, maxIterations, rate)
    testModel(sc, teInput, output, model)
    
    sc.stop
  }
  
  def testModel(sc:SparkContext, teInput:String, output:String, model:LRModel) {
    sc.textFile(teInput)
    .map(line => { 
      val prediction = model.classify(toDoubleArr(line))
      val sout = s"$line, $prediction"
      sout
    }).saveAsTextFile(output)
  }
  
  def learnModel(sc:SparkContext, trInput:String, maxIterations:Int, rate:Double) : LRModel = {
    var weights:List[Double] = List()
    
    val file = sc.textFile(trInput).cache
    
    for(iter <- 0 until maxIterations) {
      weights = learnWeights(file, weights, rate)
      val s = s"$iter: ${weights.mkString(", ")}"
      println(s)
    }
    new LRModel(weights)
  }
  
  private def learnWeights(file:RDD[String], weights:List[Double], rate:Double): List[Double] = {
    val iweights = file
    .map(line => { 
      val record = toDoubleArr(line)
      val predicted = classify(record, weights)
      val label = record(record.length-1)
      var rweights:List[Double] = List() 
      for(i <- 0 until record.length-1) {
        val w_i = rate * (label - predicted) * record(i)
        rweights = rweights :+ w_i
      }
      new RWeights(rweights)
    })
    .reduce(_ + _)
    
    if(weights.size == 0) {
      iweights.weights
    } else {
      var nweights:List[Double] = List()
      for(i <- 0 until iweights.weights.length) {
        val w = iweights.weights(i) + weights(i)
        nweights = nweights :+ w
      }
      nweights
    }
  }
  
  def classify(x:Array[Double], weights:List[Double]): Double = {
    var logit = 0.0d
    for(i <- 0 until x.length-1) {
      var w = 0.0d
      if(weights.length == x.length-1) {
        w = weights(i)
      }
      logit += (w * x(i))
    }
    sigmoid(logit)
  }
  
  def sigmoid(z:Double): Double = (1.0d / (1.0d + math.exp(-z)))
  
  def toDoubleArr(line:String): Array[Double] = {
    val arr = line.split(",")
    val vals = Array.ofDim[Double](arr.length)
    for(i <- 0 until arr.length) {
      vals(i) = arr(i).toDouble
    }
    vals
  } 
}