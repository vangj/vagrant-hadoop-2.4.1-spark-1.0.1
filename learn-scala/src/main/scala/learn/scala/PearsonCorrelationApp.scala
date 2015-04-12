package learn.scala

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object PearsonCorrelationApp {
  case class Index(i:Integer, j:Integer)
  class Computation(_x:Double, _y:Double, _xx:Double, _yy:Double, _xy:Double, _n:Double) extends Serializable {
    def x = _x
    def y = _y
    def xx = _xx
    def yy = _yy
    def xy = _xy
    def n = _n
    
    def add(that:Computation): Computation = {
      val x = this.x + that.x
      val y = this.y + that.y
      val xx = this.xx + that.xx
      val yy = this.yy + that.yy
      val xy = this.xy + that.xy
      val n = this.n + that.n
      val c = new Computation(x, y, xx, yy, xy, n)
      (c)
    }
    
    def +(that:Computation): Computation = add(that)
    
    def compute(): Double = {
      val num = xy - (x * y / n)
      val den1 = (xx - (math.pow(x, 2.0d)/n))
      val den2 = (yy - (math.pow(y, 2.0d)/n))
      val den3 = den1 * den2
      val den = math.sqrt(den3)
      val r = num / den
      r
    }
  }

  def main(args:Array[String]) {
    val input = args(0)
    val output = args(1)
    
    val sc = new SparkContext(
        new SparkConf()
        .setAppName(s"Pearson Correlation $input $output"))
    
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
        val x = arr(i).toDouble
        val y = arr(j).toDouble
        val k = new Index(i, j)
        val v = new Computation(x, y, x*x, y*y, x*y, 1.0d)
        (k, v)
      }
    })
    .reduceByKey(_ + _)
    .map(f => { 
      val k = f._1
      val v = f._2
      
      val r = v.compute
      val soutput = s"${k.i}, ${k.j}, $r"
      println(soutput)
      (r, (k.i, k.j))
    })
    .sortBy(f => { 
      val a = f._1
      math.abs(a)
    }, false)
    .map(item => s"${item._2._1}, ${item._2._2}, ${item._1}")
    .saveAsTextFile(output)
  }
}