/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package com.hp.hpl.jena.tdb.solver.stats;

import static com.hp.hpl.jena.sparql.sse.Item.addPair;
import static com.hp.hpl.jena.sparql.sse.Item.createTagged;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lib.MapUtils;
import lib.Tuple;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import com.hp.hpl.jena.sparql.sse.Item;
import com.hp.hpl.jena.sparql.sse.ItemList;
import com.hp.hpl.jena.sparql.util.NodeFactory;
import com.hp.hpl.jena.sparql.util.Utils;

import com.hp.hpl.jena.tdb.index.TripleIndex;
import com.hp.hpl.jena.tdb.pgraph.GraphTDB;
import com.hp.hpl.jena.tdb.pgraph.NodeId;

public class StatsWriter
{
    /** Gather statistics, any graph */
    public static Item gather(Graph graph)
    {
        Map<Node, Integer> predicates = new HashMap<Node, Integer>(1000) ;
        long count = 0 ;
        @SuppressWarnings("unchecked")
        Iterator<Triple> iter = (Iterator<Triple>)graph.find(Node.ANY, Node.ANY, Node.ANY) ;
        for ( ; iter.hasNext() ; )
        {
            Triple t = iter.next();
            count++ ;
            Node p = t.getPredicate() ;
            Integer num = predicates.get(p) ;
            if ( num == null )
                predicates.put(p,1) ;
            else
                predicates.put(p, num+1) ;
        }
        
        return format(predicates, count) ;
    }
    
    /** Gather statistics - faster for TDB */
    public static Item gatherTDB(GraphTDB graph)
    {
        long count = 0 ;
        Map<NodeId, Integer> predicateIds = new HashMap<NodeId, Integer>(1000) ;
        
        
        TripleIndex index = graph.getIndexSPO() ;
        Iterator<Tuple<NodeId>> iter = index.all() ;
        for ( ; iter.hasNext() ; )
        {
            Tuple<NodeId> tuple = iter.next(); 
            count++ ;
            MapUtils.increment(predicateIds, tuple.get(1)) ;
        }
        
        return statsOutput(graph, predicateIds, count) ;
    }
        
        
    private static Item statsOutput(GraphTDB graph, Map<NodeId, Integer> predicateIds, long total)
    {
        Map<Node, Integer> predicates = new HashMap<Node, Integer>(1000) ;
        for ( NodeId p : predicateIds.keySet() )
        {
            Node n = graph.getNodeTable().retrieveNodeByNodeId(p) ;
            
            // Skip these - they just clog things up!
            if ( n.getURI().startsWith("http://www.w3.org/1999/02/22-rdf-syntax-ns#_") )
                continue ;
            
            predicates.put(n, predicateIds.get(p)) ;
        }
        
        return format(predicates, total) ;
    }
     
    public static Item format(Map<Node, Integer> predicates, long count)
    {
        Item stats = Item.createList() ;
        ItemList statsList = stats.getList() ;
        statsList.add("stats") ;

//        System.out.printf("Triples  %d\n", count) ;
//        System.out.println("NodeIds") ;
//        for ( NodeId p : predicateIds.keySet() )
//            System.out.printf("%s : %d\n",p, predicateIds.get(p) ) ;

//        System.out.println("Nodes") ;
        
        Item meta = createTagged("meta") ;
        addPair(meta.getList(), "timestamp", NodeFactory.nowAsDateTime()) ;
        addPair(meta.getList(), "run@",  Utils.nowAsString()) ;
        if ( count >= 0 )
            addPair(meta.getList(), "count", NodeFactory.intToNode((int)count)) ;
        statsList.add(meta) ;
        
        for ( Node p : predicates.keySet() )
        {
            addPair(statsList, p, NodeFactory.intToNode(predicates.get(p))) ;
//            System.out.printf("%s : %d\n",n, predicateIds.get(p) ) ;
        }
        return stats ;
    }
}

/*
 * (c) Copyright 2008 Hewlett-Packard Development Company, LP
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */