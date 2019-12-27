/*
 * Copyright (c) 2018 (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.lmax.disruptor.biz.event.handler.chain;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.biz.event.DisruptorEvent;
import com.lmax.disruptor.biz.event.handler.DisruptorHandler;

public class ProxiedHandlerChain implements HandlerChain<DisruptorEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ProxiedHandlerChain.class);
	
    private ProxiedHandlerChain orig;
    private List<DisruptorHandler<DisruptorEvent>> handlers;
    private int currentPosition = 0;

    public ProxiedHandlerChain() {
        this.currentPosition = -1;
    }
    
    public ProxiedHandlerChain(ProxiedHandlerChain orig, List<DisruptorHandler<DisruptorEvent>> handlers) {
        if (orig == null) {
            throw new NullPointerException("original HandlerChain cannot be null.");
        }
        this.orig = orig;
        this.handlers = handlers;
        this.currentPosition = 0;
    }

    @Override
	public void doHandler(DisruptorEvent event) throws Exception {
        if (this.handlers == null || this.handlers.size() == this.currentPosition) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking original filter chain.");
            }
            if(this.orig != null) {
            	this.orig.doHandler(event);
            }
        } else {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Invoking wrapped filter at index [" + this.currentPosition + "]");
            }
            this.handlers.get(this.currentPosition++).doHandler(event, this);
        }
    }
    
}
