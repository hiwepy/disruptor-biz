/*
 * Copyright (c) 2010-2020, vindell (https://github.com/vindell).
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
package com.lmax.disruptor.biz.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.biz.event.DisruptorEvent;
import com.lmax.disruptor.biz.handler.chain.HandlerChain;

public class AbstractAdviceEventHandler<T extends DisruptorEvent> extends AbstractEnabledEventHandler<T> {

	protected final Logger LOG = LoggerFactory.getLogger(AbstractAdviceEventHandler.class);
	
	protected boolean preHandle(T event) throws Exception {
		return true;
	}

	protected void postHandle(T event) throws Exception {
	}

	public void afterCompletion(T event, Exception exception) throws Exception {
	}

	protected void executeChain(T event, HandlerChain<T> chain) throws Exception {
		chain.doHandler(event);
	}

	@Override
	public void doHandlerInternal(T event, HandlerChain<T> handlerChain) throws Exception {

		if (!isEnabled(event)) {
        	LOG.debug("Handler '{}' is not enabled for the current event.  Proceeding without invoking this handler.", getName());
        	// Proceed without invoking this handler...
            handlerChain.doHandler(event);
		} else {
			
			LOG.trace("Handler '{}' enabled.  Executing now.", getName());
			
			Exception exception = null;
			
			try {
	
				boolean continueChain = preHandle(event);
				if (LOG.isTraceEnabled()) {
					LOG.trace("Invoked preHandle method.  Continuing chain?: [" + continueChain + "]");
				}
				if (continueChain) {
					executeChain(event, handlerChain);
				}
				postHandle(event);
				if (LOG.isTraceEnabled()) {
					LOG.trace("Successfully invoked postHandle method");
				}
	
			} catch (Exception e) {
				exception = e;
			} finally {
				cleanup(event, exception);
			}
		}

	}

	protected void cleanup(T event, Exception existing) throws Exception {
		Exception exception = existing;
		try {
			afterCompletion(event, exception);
			if (LOG.isTraceEnabled()) {
				LOG.trace("Successfully invoked afterCompletion method.");
			}
		} catch (Exception e) {
			if (exception == null) {
				exception = e;
			} else {
				LOG.debug("afterCompletion implementation threw an exception.  This will be ignored to "
						+ "allow the original source exception to be propagated.", e);
			}
		}
	}
	
	protected boolean isEnabled(T event)
			throws Exception {
		return isEnabled();
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
