package test.scala


import aligner._
import org.scalatest.{FlatSpec, Matchers}
import reads._
import utils.CutSites


/**
  * Created by aaronmck on 6/21/16.
  */
class ConsensusTest extends FlatSpec with Matchers {
  "Consensus" should "properly find the consensus bases in a basic alignment" in {
    val fakeRead1 = SequencingRead.readFromNameAndSeq("TEST1","TTTTTTTTTT") // 10 bases
    val fakeRead2 = SequencingRead.readFromNameAndSeq("TEST2","TTTTTTTTTT") // 10 bases
    val fakeRead3 = SequencingRead.readFromNameAndSeq("TEST3","TTTTT") // 5 bases

    val cons = Consensus.consensus(Array[SequencingRead](fakeRead1,fakeRead2,fakeRead3))

    cons.length should be (10)
    cons.bases should be ("TTTTTTTTTT")
  }

  "Consensus" should "properly find the consensus bases in a basic  mixed legnth alignment with default 50% reads required" in {
    val fakeRead1 = SequencingRead.readFromNameAndSeq("TEST1","TTTTTTTTTT") // 10 bases
    val fakeRead2 = SequencingRead.readFromNameAndSeq("TEST2","TTTTTTTTT") // 9 bases
    val fakeRead3 = SequencingRead.readFromNameAndSeq("TEST3","TTTTT") // 5 bases

    val cons = Consensus.consensus(Array[SequencingRead](fakeRead1,fakeRead2,fakeRead3))

    cons.length should be (9)
    cons.bases should be ("TTTTTTTTT")
  }

  "Consensus" should "properly find the consensus bases when errors are present" in {
    val fakeRead1 = SequencingRead.readFromNameAndSeq("TEST1","TATTATTTTT") // 10 bases
    val fakeRead2 = SequencingRead.readFromNameAndSeq("TEST2","TTTTTTTTT") // 9 bases
    val fakeRead3 = SequencingRead.readFromNameAndSeq("TEST3","TTTTT") // 5 bases

    val cons = Consensus.consensus(Array[SequencingRead](fakeRead1,fakeRead2,fakeRead3))

    cons.length should be (9)
    cons.bases should be ("TTTTTTTTT")
  }

  "Consensus" should "properly find the consensus bases when multiple errors are present, and take the first dominant base seen" in {
    val fakeRead1 = SequencingRead.readFromNameAndSeq("TEST1","TATTATTATT") // 10 bases
    val fakeRead2 = SequencingRead.readFromNameAndSeq("TEST2","TTTTTTTTT") // 9 bases
    val fakeRead3 = SequencingRead.readFromNameAndSeq("TEST3","TTTTT") // 5 bases

    val cons = Consensus.consensus(Array[SequencingRead](fakeRead1,fakeRead2,fakeRead3))

    cons.length should be (9)
    cons.bases should be ("TTTTTTTAT")
  }

  "Consensus" should "properly find the consensus bases when multiple errors are present, and take the first dominant base seen (reverse of before)" in {
    val fakeRead1 = SequencingRead.readFromNameAndSeq("TEST1","TATTATTTTT") // 10 bases
    val fakeRead2 = SequencingRead.readFromNameAndSeq("TEST2","TTTTTTTAT") // 9 bases
    val fakeRead3 = SequencingRead.readFromNameAndSeq("TEST3","TTTTT") // 5 bases

    val cons = Consensus.consensus(Array[SequencingRead](fakeRead1,fakeRead2,fakeRead3))

    cons.length should be (9)
    cons.bases should be ("TTTTTTTAT")
  }

  "Consensus" should "properly find the consensus from a single read" in {
    val fakeRead1 = SequencingRead.readFromNameAndSeq("TEST1","TATTATTTTT") // 10 bases

    val cons = Consensus.consensus(Array[SequencingRead](fakeRead1))

    cons.length should be (10)
    cons.bases should be ("TATTATTTTT")
  }
}
