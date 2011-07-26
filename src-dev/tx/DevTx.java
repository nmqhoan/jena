package tx;


public class DevTx
{
    // --------
    // Tasks:
    // * scan to commit, enact, scan to commit, enact, ....
    // * Check journal truncates to last commit.
    //   Journal needs reset markers

    // * Node writing happens during prepare() regardless of blockers and locks.
    //   This is OK if done safely (locked against new transactions)
    // * Some kind of call after commit for clear up.
    //   Call when really commited.
    // * CRC and bullet-proof read of Journal.
    // * Params.
    // * Assembler
    // * Dataset API
    // * UUID per committed version to support etags
    // * Promote => duplicate even when not necessary.  BlockMgr property.
    
    
    // Tidy up:
    // A DatasetGraphTDB is 3 NodeTupleTables.  Build as such.
    //    Triple/Quad/Prefix table to take a NodeTupleTable.

    // DatasetControl
    //   Change able and do ReadOnly this way.
    //   .setReadMode = affect (shared ) DatasetControl
    
    // ?? Journal for BlockMgrs only.
    //  System journal is just commits/aborts.
    
    // Iterator tracking.
    // NodeTupleTable.find [NodeTupleTableConcrete]
    //     Iterator<Tuple<NodeId>> find(Tuple<NodeId> tuple) ==> checkIterator: 
    //     **** Catch in NodeTupleTable.find
    
    // autocommit mode.
    //   Better to also wrap reading from the parser?
    //   WriteLock => start xAction.
    
    // DSG.add(Quad(tripleInQuad, triple)) does not affect default graph.
    
    // * Check syncs NodeTupleTable, NodeTable keep a dirty flag 
    // * B+Tree and caching root block.
    //   BPT created per transaction so safe (?).
   
    // Tidy up 
    //   See HACK (BPTreeNode)
    //   See [TxTDB:PATCH-UP]
    //   See [TxTDB:TODO]
    //   See FREE
    //   See [ITER]
    
    // Optimizations:
    //   ByteBuffer.allocateDirect + pooling
    //     http://mail-archives.apache.org/mod_mbox/mina-dev/200804.mbox/%3C47F90DF0.6050101@gmail.com%3E
    //     http://mail-archives.apache.org/mod_mbox/mina-dev/200804.mbox/%3Cloom.20080407T064019-708@post.gmane.org%3E

    // Other:
    //   Sort out IndexBulder/IndexFactory/(IndexMaker in test)
    // TDB 0.8.10 is rev 8718; TxTDB forked at 8731
    // Diff of SF ref 8718 to Apache cross over applied. (src/ only)
    // Now Apache: rev 1124661 
}
