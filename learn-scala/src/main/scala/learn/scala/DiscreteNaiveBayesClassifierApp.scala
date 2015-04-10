package learn.scala

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import scala.collection.mutable.HashMap
import scala.collection.Set

object DiscreteNaiveBayesClassifierApp {
  case class Index(index:Int, clazz:String, value:String, clazzCount:Boolean)
  class NBModel() extends Serializable {
    val vcounts = new HashMap[Index, Int]
    val ccounts = new HashMap[String, Int]
    var total = 0.0d
    
    def addEntry(index:Index, n:Int) {
      if(index.clazzCount) {
        ccounts.put(index.value, n)
        total += n
      } else {
        vcounts.put(index, n)
      }
      
      println(s"${index.index}, ${index.clazz}, ${index.value}, ${index.clazzCount}, ${n}")
    }
    
    def classify(line:String): HashMap[String, Object] = {
      val arr = line.split(",")
      val results = new HashMap[String, Object]
      ccounts.keySet.foreach(clazz => { 
        val N_c = ccounts.get(clazz)
        var score = math.log(N_c.get) - math.log(total)
        for(i <- 0 until arr.length-1) {
          val N_xc = vcounts.get(new Index(i, clazz, arr(i), false))
          score += (math.log(N_xc.get) - math.log(N_c.get))
        }
        results.put(clazz, score.toString)
        println(s"$clazz, $score : $line")
      })
      results.put("_instance_", line)
      results
    }
  }
  
  def main(args:Array[String]) {
    val trInput = args(0)
    val teInput = args(1)
    val output = args(2)
    
    val sc = new SparkContext(
        new SparkConf()
        	.setAppName(s"Naive Bayes Classifier $trInput $teInput $output")
        )
    
    val model = learnModel(sc, trInput)
    testModel(sc, model, teInput, output)
    
    sc.stop
  }
  
  def testModel(sc:SparkContext, model:NBModel, teInput:String, output:String) = {
    sc.textFile(teInput)
    .map(line => { 
      model.classify(line)
    }).saveAsTextFile(output)
  }
  
  def learnModel(sc:SparkContext, trInput:String): NBModel = {
    val model = new NBModel
    sc.textFile(trInput)
    .flatMap(line => { 
      val arr = line.split(",")
      val c = arr(arr.length-1)
      
      for(i <- 0 until arr.length) yield {
        val v = arr(i)
        val cc = if(i == arr.length-1) true else false
        (new Index(i, c, v, cc), 1)
      }
    })
    .reduceByKey((a:Int, b:Int) => a + b)
    .collect
    .foreach(item => {
      model.addEntry(item._1, item._2)
    })    
    model
  }
}