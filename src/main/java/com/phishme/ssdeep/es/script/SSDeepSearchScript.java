/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.phishme.ssdeep.es.script;

import com.phishme.ssdeep.SSDeep;
import com.phishme.ssdeep.SSDeepHash;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.index.fielddata.ScriptDocValues;
import org.elasticsearch.script.AbstractLongSearchScript;
import org.elasticsearch.script.ExecutableScript;
import org.elasticsearch.script.NativeScriptFactory;
import org.elasticsearch.script.ScriptException;
import org.elasticsearch.search.lookup.LeafDocLookup;

import java.util.Map;

/**
 * Implementation of the SSDeep script plugin.
 *
 * @author Russell Francis &lt;russell.francis@phishme.com&gt;
 */
public class SSDeepSearchScript extends AbstractLongSearchScript {

    static private final SSDeep ssDeep = new SSDeep();

    static public class Factory implements NativeScriptFactory {

        /**
         * Construct a new SSDeepSearchScript instance based on the provided parameters.
         *
         * The parameters must include "hash" which is an SSDeep hash string and "field" which is
         * the document field which should be used for comparison when attempting to identify similar documents.
         *
         * @param params A map of all the parameters provided to the script.
         * @return A new SSDeepSearchScript instance with the provided parameters.
         * @throws ScriptException If either the "hash" or "field" parameters are not present.
         * @throws IllegalArgumentException If the "hash" value could not be interpreted as an SSDeep hash.
         */
        @Override
        public ExecutableScript newScript(@Nullable Map<String, Object> params) {
            final String fieldName = params == null ? null : XContentMapValues.nodeStringValue(params.get("field"), null);
            if (fieldName == null) {
                throw new ScriptException("Missing the field parameter.");
            }

            final String hash = XContentMapValues.nodeStringValue(params.get("hash"), null);
            if (hash == null) {
                throw new ScriptException("Missing the hash parameter.");
            }

            return new SSDeepSearchScript(fieldName, hash);
        }

        /**
         * This script doesn't need any computed scores at this point.
         *
         * @return false
         */
        @Override
        public boolean needsScores() {
            return false;
        }
    }

    final private String fieldName;
    final private SSDeepHash hash;

    /**
     * Factory creates this script on every
     *
     * @param fieldName the name of the field that should be checked
     */
    private SSDeepSearchScript(final String fieldName, final String hash) {
        this.fieldName = fieldName;
        this.hash = ssDeep.fromString(hash);
    }

    /**
     * Return a long value from 0 to 100 indicating how well the provided hash matches this document 100 is a very good
     * match, 0 is a terrible match.
     *
     * @return A number between 0 and 100 inclusive indicating how well the ssdeep hashes match.
     */
    @Override
    public long runAsLong() {
        final LeafDocLookup doc = doc();
        if (doc != null) {
            final Object o = doc.get(fieldName);
            if (o != null && o instanceof ScriptDocValues.Strings) {
                final String docHash = ((ScriptDocValues.Strings) o).getValue();
                try {
                    return hash.compare(ssDeep.fromString(docHash));
                } catch (Exception e) {
                    // ignore and return 0.
                }
            }
            // Within elasticsearch accessing the source document is frowned upon and to search by a field it must
            // be indexed.  Even though the index for our case isn't very interesting we keep to this convention and
            // require the ssdeep hash to be stored in the index as a string.  If we change our mind later, this
            // code allows pulling the script from the source document if available.
            //else {
            //    o = source().get(fieldName);
            //    if (o != null && o instanceof String) {
            //        return hash.compare(ssDeep.fromString((String)o));
            //    }
            //}
        }
        return 0L;
    }
}
