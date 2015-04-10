package learn.scala

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object CountKeywordApp {
  def main(args: Array[String]) {
    val input = args(0)
    val keyword = args(1)
    val conf = new SparkConf().setAppName(s"Count $keyword")
    val sc = new SparkContext(conf)
    val bookData = sc.textFile(input, 2).cache()
    val count = bookData.filter(line => line.contains(keyword)).count()
    println(s"total lines with '$keyword' is $count")
    sc.stop
  }
}