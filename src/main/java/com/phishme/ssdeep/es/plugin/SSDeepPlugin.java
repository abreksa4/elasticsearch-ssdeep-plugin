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
package com.phishme.ssdeep.es.plugin;

import com.phishme.ssdeep.es.script.SSDeepSearchScript;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.script.ScriptModule;

/**
 * Descriptor for the SSDeep script plugin.
 *
 * @author Russell Francis &lt;russell.francis@phishme.com&gt;
 */
public class SSDeepPlugin extends Plugin {
    /**
     * The name of the plugin.
     *
     * @return The name of the plugin.
     */
    @Override
    public String name() {
        return "ssdeep";
    }

    /**
     * The description of the plugin.
     *
     * @return The description of the plugin.
     */
    @Override
    public String description() {
        return "SSDeep script plugin for elasticsearch.";
    }

    /**
     * Required bootstrapping to add the script plugin to the supported native scripts of the ES runtime.
     *
     * @param module The ScriptModule instance provided by the runtime.
     */
    public void onModule(ScriptModule module) {
        module.registerScript("ssdeep", SSDeepSearchScript.Factory.class);
    }
}
