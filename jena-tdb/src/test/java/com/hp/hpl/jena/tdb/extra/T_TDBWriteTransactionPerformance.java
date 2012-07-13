/*
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

/* ******************************************************************************
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corporation 2011. All Rights Reserved.
 *
 * Note to U.S. Government Users Restricted Rights:  Use,
 * duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp.
 * *****************************************************************************/

// Submitted JENA-256 

package com.hp.hpl.jena.tdb.extra ;

import java.util.ArrayList ;
import java.util.List ;

import org.openjena.atlas.lib.FileOps ;
import org.openjena.atlas.logging.Log ;

import com.hp.hpl.jena.query.Dataset ;
import com.hp.hpl.jena.query.ReadWrite ;
import com.hp.hpl.jena.rdf.model.Model ;
import com.hp.hpl.jena.rdf.model.Property ;
import com.hp.hpl.jena.rdf.model.Resource ;
import com.hp.hpl.jena.tdb.TDBFactory ;
import com.hp.hpl.jena.tdb.base.block.FileMode ;
import com.hp.hpl.jena.tdb.sys.SystemTDB ;

public class T_TDBWriteTransactionPerformance {

	private final static int TOTAL = 100;
	final static String INDEX_INFO_SUBJECT =   "http://test.net/xmlns/test/1.0/Triple-Indexer";
	final static String TIMESTAMP_PREDICATE =  "http://test.net/xmlns/test/1.0/lastProcessedTimestamp";
	final static String URI_PREDICATE =        "http://test.net/xmlns/test/1.0/lastProcessedUri";
	final static String VERSION_PREDICATE =    "http://test.net/xmlns/test/1.0/indexVersion";
	final static String INDEX_SIZE_PREDICATE = "http://test.net/xmlns/test/1.0/indexSize";

    public static void main(String[] args) {

	    Log.setLog4j() ;
	    
//		if (args.length == 0) {
//			System.out.println("Provide index location");
//			return;
//		}

	    String location = "DBX" ;
	    FileOps.ensureDir(location) ;
	    FileOps.clearDirectory(location) ;
	    
	    //Submitted without .... 
	    if ( true )
	        SystemTDB.setFileMode(FileMode.direct) ;
	    
	    //String location = args[0]; // + "/" + UUID.randomUUID().toString();

		//String baseGraphName = "com.ibm.test.graphNamePrefix.";   

		long totalExecTime = 0L;
		long size = 0;
		Dataset dataset = TDBFactory.createDataset(location);
		Dataset dataset1 = TDBFactory.createDataset(location);
		boolean bracketWithReader = false ;
		
		if ( bracketWithReader )
		    dataset1.begin(ReadWrite.READ) ;
		
		for (int i = 0; i < TOTAL; i++) {
			List<String> lastProcessedUris = new ArrayList<String>();
			for (int j = 0; j < 10*i; j++) {
				String lastProcessedUri = "http://test.net/xmlns/test/1.0/someUri" + j;
				lastProcessedUris.add(lastProcessedUri);
			}
			//Dataset dataset = TDBFactory.createDataset(location);
			//String graphName = baseGraphName + i;
			long t = System.currentTimeMillis();

			try {
				dataset.begin(ReadWrite.WRITE);
				Model m = dataset.getDefaultModel();

				m.removeAll();
				Resource subject = m.createResource(INDEX_INFO_SUBJECT);
				Property predicate = m.createProperty(TIMESTAMP_PREDICATE);
				m.addLiteral(subject, predicate, System.currentTimeMillis());
				predicate = m.createProperty(URI_PREDICATE);
				for (String uri : lastProcessedUris) {
					m.add(subject, predicate, m.createResource(uri));
				}
				predicate = m.createProperty(VERSION_PREDICATE);
				m.addLiteral(subject, predicate, 1.0);

				size += m.size() + 1;

				predicate = m.createProperty(INDEX_SIZE_PREDICATE);
				m.addLiteral(subject, predicate, size);

				dataset.commit();
			} catch (Throwable e) {
				dataset.abort();
				throw new RuntimeException(e);
			} finally {
				dataset.end();
				long writeOperationDuration = System.currentTimeMillis() - t;
				totalExecTime += writeOperationDuration;
				System.out.println("Write operation " + i + " took " + writeOperationDuration + "ms");
			}
		}
        if ( bracketWithReader )
            dataset1.end() ;

		System.out.println("All " + TOTAL + " write operations wrote " + size + " triples and took " + totalExecTime + "ms");
	}

}
