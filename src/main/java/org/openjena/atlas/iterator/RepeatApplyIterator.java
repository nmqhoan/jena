/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openjena.atlas.iterator;

import java.util.Iterator ;
import java.util.NoSuchElementException ;

import org.openjena.atlas.lib.Closeable ;

import com.hp.hpl.jena.sparql.util.Utils ;

public abstract class RepeatApplyIterator<T> implements Iterator<T>, Closeable
{
    private Iterator<T> input ;
    private boolean finished = false ;
    private Iterator<T> currentStage = null ;

    protected RepeatApplyIterator(Iterator<T> input)
    {
        this.input = input ;
    }

    @Override
    public boolean hasNext()
    {
        if  ( finished )
            return false ;
        for ( ;; )
        {
            if ( currentStage == null && input.hasNext() )
            {
                T nextItem = input.next();
                currentStage = makeNextStage(nextItem) ;
            }
            
            if ( currentStage == null  )
            {
                finished = true ;
                return false ;
            }
            
            if ( currentStage.hasNext() )
                return true ;
            
            currentStage = null ;
        }
    }

    protected abstract Iterator<T> makeNextStage(T t) ;
    
    @Override
    public T next()
    {
        if ( ! hasNext() )
            throw new NoSuchElementException(Utils.className(this)+".next()/finished") ;
        return currentStage.next() ;
    }

    @Override
    public final void remove()
    { throw new UnsupportedOperationException() ; }
    
    @Override
    public void close()
    {
        Iter.close(input) ;
    }
}