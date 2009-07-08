/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
 * All rights reserved.
 * [See end of file]
 */

package atlas.lib;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import atlas.lib.AtlasException;


/**
 * @author Andy Seaborne
 */

public class CacheLRU<K,V> implements Cache<K,V>
{
    private CacheImpl<K,V> cache ;
    
    public CacheLRU(float loadFactor, int maxSize) { cache = new CacheImpl<K, V>(loadFactor, maxSize) ; }

    //@Override
    public void clear()
    { cache.clear() ; }

    //@Override
    public boolean containsKey(K key)
    {
        return cache.containsKey(key) ;
    }

    //@Override
    //public V getObject(K key, boolean exclusive)
    public V get(K key)
    {
        return cache.get(key) ;
    }

    //@Override
    public void promote(K key)
    {
        throw new AtlasException("Not implemented") ; 
    }

    //@Override
    public void put(K key, V thing)
    {
        cache.put(key, thing) ;
    }

    //@Override
    public void remove(K key)
    {
        cache.remove(key) ;
    }

    //@Override
    public void returnObject(K key)
    {
        throw new AtlasException("Not implemented") ;
    }
    
    //@Override
    public long size()
    {
        return cache.size() ;
    }

    //@Override
    public Iterator<K> keys()
    {
        return cache.keySet().iterator() ;
    }

    //@Override
    public boolean isEmpty()
    {
        return cache.isEmpty() ;
    }

    /** Callback for entries when dropped from the cache */
    public void setDropHandler(ActionKeyValue<K,V> dropHandler)
    {
        cache.setDropHandler(dropHandler) ;
    }

    public static class CacheImpl<K,V> extends LinkedHashMap<K, V>
    {
        // Use? ArrayBlockingQueue
        int maxEntries ; 
        ActionKeyValue<K,V> dropHandler = null ;
    
        public CacheImpl(int maxSize)
        {
            this(0.75f, maxSize) ;
        }
    
        public CacheImpl(float loadFactor, int maxSize)
        {
            // True => Access order, which is what makes it LRU
    
            // Initial size is max size + slop, rounded up, for the load factor
            // i.e. it allocate the space needed once at create time.
    
            super( Math.round(maxSize/loadFactor+0.5f)+1, loadFactor, true) ;
            // which is also (int)Math.floor(a + 1f)
            // and hence can be one larger than needed.  But safer than one less.
            // +1 is the need for the added entry before the removing the "eldest"
            maxEntries = maxSize ;
    
    
        }
    
        /** Callback for entries when dropped from the cache */
        public void setDropHandler(ActionKeyValue<K,V> dropHandler)
        {
            this.dropHandler = dropHandler ;
        }
    
        @Override
        protected boolean removeEldestEntry(Map.Entry<K,V> eldest) 
        {
            // Overshoots by one - the new entry is added, then this called.
            // Initial capacity adjusted to allow for this.
    
            boolean b = ( size() > maxEntries ) ;
            if ( b && dropHandler != null )
                dropHandler.apply(eldest.getKey(), eldest.getValue()) ;
            return b ;
        }
    }
}

/*
 * (c) Copyright 2007, 2008, 2009 Hewlett-Packard Development Company, LP
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