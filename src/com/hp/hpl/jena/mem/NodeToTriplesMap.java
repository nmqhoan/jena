/*
  (c) Copyright 2003, Hewlett-Packard Development Company, LP, all rights reserved.
  [See end of file]
  $Id: NodeToTriplesMap.java,v 1.3 2003-11-17 07:24:50 chris-dollin Exp $
*/

package com.hp.hpl.jena.mem;

import java.util.*;

import junit.framework.TestCase;

import com.hp.hpl.jena.graph.*;
import com.hp.hpl.jena.util.iterator.NullIterator;

/**
	NodeToTriplesMap: a map from nodes to sets of triples, where the set is implemented
    as a linked list [the context allows this].

	@author kers
*/
public class NodeToTriplesMap {
    HashMap map = new HashMap();

    static class Bunch extends LinkedList {}
    
    public void add( Node o, Triple t ) {
        Bunch l = (Bunch) map.get( o );
        if (l==null) map.put( o, l = new Bunch() );
        l.add( t ); // l.add( t );
    }

    public void remove(Node o, Triple t ) {
        Bunch l = (Bunch) map.get( o );
        if (l != null) {
            l.remove( t );
            if (l.size() == 0) map.put( o, null );
        }
    }

    public Iterator iterator(Node o) {
        Bunch l = (Bunch) map.get( o );
        if (l==null) {
            return NullIterator.instance;
        } else {
            return l.iterator();
        }
    }
}
/*
    (c) Copyright 2003, Hewlett-Packard Development Company, LP
    All rights reserved.

    Redistribution and use in source and binary forms, with or without
    modification, are permitted provided that the following conditions
    are met:

    1. Redistributions of source code must retain the above copyright
       notice, this list of conditions and the following disclaimer.

    2. Redistributions in binary form must reproduce the above copyright
       notice, this list of conditions and the following disclaimer in the
       documentation and/or other materials provided with the distribution.

    3. The name of the author may not be used to endorse or promote products
       derived from this software without specific prior written permission.

    THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
    IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
    OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
    IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
    INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
    NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
    DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
    THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
    (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
    THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/