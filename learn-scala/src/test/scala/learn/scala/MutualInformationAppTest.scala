package learn.scala

import org.scalatest.Assertions
import org.junit.Test
import org.junit.Assert

class MutualInformationAppTest extends Assertions {

  @Test def testTotalAndComputation() {
    val counts = new MutualInformationApp.Counts(new MutualInformationApp.Index(0, 1))
    counts.addEntry(new MutualInformationApp.Combo("a","a"), 4)
    counts.addEntry(new MutualInformationApp.Combo("a","b"), 1)
    counts.addEntry(new MutualInformationApp.Combo("b","a"), 2)
    counts.addEntry(new MutualInformationApp.Combo("b","b"), 3)
    
    assert(10.0d == counts.total)
    
    val result = counts.compute
    Assert.assertEquals(0.08630462174d, result.mi, 0.000001)
  }
  
  @Test def testAdd() {
    val counts1 = new MutualInformationApp.Counts(new MutualInformationApp.Index(0, 1))
    val counts2 = new MutualInformationApp.Counts(new MutualInformationApp.Index(0, 1))
    
    counts1.addEntry(new MutualInformationApp.Combo("a","a"), 4)
    counts1.addEntry(new MutualInformationApp.Combo("a","b"), 1)
    
    counts2.addEntry(new MutualInformationApp.Combo("b","a"), 2)
    counts2.addEntry(new MutualInformationApp.Combo("b","b"), 3)
    
    val counts = counts1.add(counts2)
    assert(10.0d == counts.total)
    
    val result = counts.compute
    Assert.assertEquals(0.08630462174d, result.mi, 0.000001)
  }
}