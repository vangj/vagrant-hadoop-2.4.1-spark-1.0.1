package learn.scala

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import scala.collection.mutable.HashMap

object MutualInformationApp {
  case class Index(i:Int, j:Int)
  case class Combo(x:String, y:String)
  case class Result(i:Int, j:Int, mi:Double)
  
  class Counts(_index:Index) extends Serializable {
    def index = _index
    val counts = new HashMap[Combo, Int]
    var n = 0.0d
    
    def compute(): Result = {
      var mi = 0.0d
      counts.foreach(f => {
        val combo = f._1
        if(!combo.x.isEmpty() && !combo.y.isEmpty()) {
         val N_xy = f._2
         val N_x = counts.get(new Combo(combo.x, "")).get
         val N_y = counts.get(new Combo("", combo.y)).get
         val temp = math.log(N_xy)
         val v = (N_xy / n) * (math.log(N_xy) + math.log(n) - math.log(N_x) - math.log(N_y))
         mi += v 
        }
      })
      new Result(index.i, index.j, mi)
    }
    
    def total():Double = this.n
    
    def addEntry(combo:Combo, n:Int) {
      increment(combo, n)
      increment(new Combo(combo.x, ""), n)
      increment(new Combo("", combo.y), n)
      
      this.n += n
    }
    
    private def increment(combo:Combo, n:Int) {
      var total = 0
      val v = counts.get(combo)
      if(v.isDefined)
        total = v.get
      total += n
      counts.put(combo, total)
    }
    
    def add(that:Counts): Counts = {
      if(this.index.i != that.index.i || this.index.j != that.index.j)
        throw new RuntimeException("Indices are different, cannot add counts!")
      
      var combos:scala.collection.mutable.Set[Combo] = scala.collection.mutable.Set()
      this.counts.keySet.foreach(combo => combos.add(combo))
      that.counts.keySet.foreach(combo => combos.add(combo))
      
      val count = new Counts(new Index(this.index.i, this.index.j))
      combos.foreach(combo => { 
        if(!combo.x.isEmpty() && !combo.y.isEmpty()) {
         var n1 = 0
         var n2 = 0
         if(this.counts.get(combo).isDefined)
           n1 = this.counts.get(combo).get
         if(that.counts.get(combo).isDefined)
           n2 = that.counts.get(combo).get
         val n = n1 + n2
         count.addEntry(new Combo(combo.x, combo.y), n) 
        }
      })
      count
    }
    
    def +(that:Counts): Counts = add(that)
  }
  
  def main(args:Array[String]) {
    val input = args(0)
    val output = args(1)
    
    val sc = new SparkContext(
        new SparkConf()
        .setAppName(s"Mutual Information $input $output"))
    
    start(input, output, sc)
    
    sc.stop
  }
  
  def start(input:String, output:String, sc:SparkContext) {
    sc.textFile(input)
    .flatMap(line => { 
      val arr = line.split(",")
      for {
        i <- 0 until arr.length
        j <- (i+1) until arr.length
      } yield {
        val x = arr(i)
        val y = arr(j)
        val k = new Index(i, j)
        val v = new Counts(k)
        v.addEntry(new Combo(x, y), 1)
        
        (k, v)
      }
    })
    .reduceByKey(_ + _)
    .map(f => {
      val k = f._1
      val v = f._2
      val r = v.compute
      (r.mi, (r.i, r.j))
    })
    .sortByKey(false)
    .map(item => s"${item._2._1}, ${item._2._2}, ${item._1}")
    .saveAsTextFile(output)
  }
}