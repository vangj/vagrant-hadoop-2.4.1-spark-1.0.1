package learn.scala

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object PearsonCorrelationApp {
  case class Index(i:Integer, j:Integer)
  case class Computation(x:Double,y:Double,xx:Double, yy:Double, xy:Double, n:Double)

  def main(args:Array[String]) {
    val input = args(0)
    val output = args(1)
    
    val sc = new SparkContext(
        new SparkConf()
        .setAppName(s"Pearson Correlation $input $output"))
    
    sc.textFile(input)
    .flatMap(line => {
      val arr = line.split(",")
      for {
        i <- 0 until arr.length
        j <- (i+1) until arr.length
      } yield {
        val x = arr(i).toDouble
        val y = arr(j).toDouble
        val xx = x * x
        val yy = y * y
        val xy = x * y
        val k = new Index(i, j)
        val v = new Computation(x, y, xx, yy, xy, 1.0d)
        (k, v)
      }
    })
    .reduceByKey((a:Computation, b:Computation) => {
      val x = a.x + b.x
      val y = a.y + b.y
      val xx = a.xx + b.xx
      val yy = a.yy + b.yy
      val xy = a.xy + b.xy
      val n = a.n + b.n
      val c = new Computation(x, y, xx, yy, xy, n)
      (c)
    })
    .map(f => { 
      val k = f._1
      val v = f._2
      
      val num = v.xy - (v.x * v.y / v.n)
      val den1 = (v.xx - (math.pow(v.x, 2.0d)/v.n))
      val den2 = (v.yy - (math.pow(v.y, 2.0d)/v.n))
      val den3 = den1 * den2
      val den = math.sqrt(den3)
      val r = num / den
      val i = k.i
      val j = k.j
      val soutput = s"$i, $j, $r"
      println(soutput)
      (soutput)
    }).saveAsTextFile(output)
    
    sc.stop
  }
}