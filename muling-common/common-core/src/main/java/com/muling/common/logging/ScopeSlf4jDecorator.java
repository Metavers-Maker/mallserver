/**
 * Copyright 2018-2019 The OpenTracing Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.muling.common.logging;

import io.opentracing.Scope;
import io.opentracing.Span;
import org.slf4j.MDC;

import java.util.Map;

/**
 * @author <a href="mailto:taylor.tian@ericsson.com">Taylor Tian</a>
 */
public class ScopeSlf4jDecorator implements Scope {
    private Scope scope;
    private Map<String, String> previousContext;

    public ScopeSlf4jDecorator(Scope scope, Map previousContext) {
        this.scope = scope;
        this.previousContext = previousContext;
    }

    @Override
    public void close() {
        scope.close();

        for (Map.Entry<String, String> entry : previousContext.entrySet()) {
            MDC.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    @Deprecated
    public Span span() {
        return scope.span();
    }
}

