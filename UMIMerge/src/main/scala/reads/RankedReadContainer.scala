package reads

import scala.collection.mutable

/**
  * evaluate reads on the fly, limiting our container to a specified size of high-quality reads, but recording the total numbers we saw
  */
class RankedReadContainer(umi: String, maxSize: Int) {
  var totalReads = 0
  var totalPassedReads = 0
  var noPrimer1 = 0
  var noPrimer2 = 0

  val maxSz = maxSize
  var pQ = new mutable.PriorityQueue[SortedReads]()

  def size() = pQ.size

  /**
    * add a read pair our sorting container, dropping low-quailty reads if we've exceeded our storage capacity
    *
    * @param seq1            the first sequence read
    * @param containsPrimer1 does the first read contain the primer?
    * @param seq2            the second sequence read
    * @param containsPrimer2 does the second read contain the primer?
    */
  def addRead(seq1: SequencingRead, containsPrimer1: Boolean, seq2: SequencingRead, containsPrimer2: Boolean): Unit = {
    totalReads += 1

    if (containsPrimer1 && containsPrimer2) {
      totalPassedReads += 1
      pQ += SortedReads(seq1, seq2)
      while (pQ.size > maxSz)
        pQ.dequeue()
    } else {
      if (!containsPrimer1)
        noPrimer1 += 1
      if (!containsPrimer2)
        noPrimer2 += 1
    }
  }

  def addRead(read12: SortedReads, containsPrimer1: Boolean, containsPrimer2: Boolean): Unit = {
    addRead(read12.read1, containsPrimer1, read12.read2, containsPrimer2)
  }

  /**
    * make a paired array set from the reads
    *
    * @return the paired set
    */
  def toPairedFWDREV(): Tuple2[Array[SequencingRead], Array[SequencingRead]] = {
    val readFWD = new mutable.ArrayBuffer[SequencingRead]()
    val readREV = new mutable.ArrayBuffer[SequencingRead]()

    pQ.foreach { readPair => {
      readFWD += readPair.read1
      readREV += readPair.read2
    }
    }
    return (readFWD.toArray, readREV.toArray)
  }
}

// a case class container for pairs of reads -- we use this to sort by length, given that reads are generally quality trimmed
case class SortedReads(read1: SequencingRead, read2: SequencingRead, rankByQual: Boolean = true) extends Ordered[SortedReads] {

  // here we have a choice, we can either rank by the average qual over the read, or the average length
  val totalAverageQual = (read1.averageQual() * read1.length + read2.averageQual() * read2.length) / (read1.length + read2.length).toDouble
  val totalAverageLength = (read1.length + read2.length) / (2.0)

  // choose what to rank by -- qual or length
  val rankVal = if (rankByQual) totalAverageQual else totalAverageLength

  // compare the events in reverse order -- we want to drop sorted reads in anti qual order
  def compare(that: SortedReads): Int =
    if (this.rankVal == that.rankVal)
      this.read1.length - that.read1.length
    else
      that.rankVal.toInt - this.rankVal.toInt

}