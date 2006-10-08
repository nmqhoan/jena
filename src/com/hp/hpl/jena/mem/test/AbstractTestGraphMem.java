package com.hp.hpl.jena.mem.test;

import java.util.Iterator;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.graph.impl.SimpleReifier;
import com.hp.hpl.jena.graph.test.AbstractTestGraph;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class AbstractTestGraphMem extends AbstractTestGraph
    {
    public AbstractTestGraphMem(String name)
        { super( name ); }
    
    public void testClosesReifier()
        {
        Graph g = getGraph();
        SimpleReifier r = (SimpleReifier) g.getReifier();
        g.close();
        assertTrue( r.isClosed() );
        }
    
    public void testBrokenIndexes()
        {
        Graph g = getGraphWith( "x R y; x S z" );
        ExtendedIterator it = g.find( Node.ANY, Node.ANY, Node.ANY );
        it.removeNext(); it.removeNext();
        assertFalse( g.find( node( "x" ), Node.ANY, Node.ANY ).hasNext() );
        assertFalse( g.find( Node.ANY, node( "R" ), Node.ANY ).hasNext() );
        assertFalse( g.find( Node.ANY, Node.ANY, node( "y" ) ).hasNext() );
        }   
            
    public void testBrokenSubject()
        {
        Graph g = getGraphWith( "x brokenSubject y" );
        ExtendedIterator it = g.find( node( "x" ), Node.ANY, Node.ANY );
        it.removeNext();
        assertFalse( g.find( Node.ANY, Node.ANY, Node.ANY ).hasNext() );
        }
        
    public void testBrokenPredicate()
        {
        Graph g = getGraphWith( "x brokenPredicate y" );
        ExtendedIterator it = g.find( Node.ANY, node( "brokenPredicate"), Node.ANY );
        it.removeNext();
        assertFalse( g.find( Node.ANY, Node.ANY, Node.ANY ).hasNext() );
        }
        
    public void testBrokenObject()
        {
        Graph g = getGraphWith( "x brokenObject y" );
        ExtendedIterator it = g.find( Node.ANY, Node.ANY, node( "y" ) );
        it.removeNext();
        assertFalse( g.find( Node.ANY, Node.ANY, Node.ANY ).hasNext() );
        }
    
    public void testSizeAfterRemove() 
        {
        Graph g = getGraphWith( "x p y" );
        ExtendedIterator it = g.find( triple( "x ?? ??" ) );
        it.removeNext();
        assertEquals( 0, g.size() );        
        }
    
    public void testUnnecessaryMatches() 
        {
        Node special = new Node_URI( "eg:foo" ) 
            {
            public boolean matches( Node s ) 
                {
                fail( "Matched called superfluously." );
                return true;
                }
            };
        Graph g = getGraphWith( "x p y" );
        g.add( new Triple( special, special, special ) );
        exhaust( g.find( special, Node.ANY, Node.ANY ) );
        exhaust( g.find( Node.ANY, special, Node.ANY ) );
        exhaust( g.find( Node.ANY, Node.ANY, special ) );
    }
    
    protected void exhaust( Iterator it )
        { while (it.hasNext()) it.next(); }
    }
