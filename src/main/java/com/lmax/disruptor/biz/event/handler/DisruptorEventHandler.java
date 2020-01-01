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
package com.lmax.disruptor.biz.event.handler;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.biz.event.DisruptorEvent;
import com.lmax.disruptor.biz.event.handler.chain.HandlerChain;
import com.lmax.disruptor.biz.event.handler.chain.HandlerChainResolver;
import com.lmax.disruptor.biz.event.handler.chain.ProxiedHandlerChain;

/*
 * Disruptor 事件分发实现
 */
public class DisruptorEventHandler extends AbstractRouteableEventHandler<DisruptorEvent> implements EventHandler<DisruptorEvent>{
	

	public DisruptorEventHandler(HandlerChainResolver<DisruptorEvent> filterChainResolver) {
		super(filterChainResolver);
	}
	
	/*
	 * 责任链入口
	 */
	@Override
	public void onEvent(DisruptorEvent event, long sequence, boolean endOfBatch) throws Exception {
		//构造原始链对象
		HandlerChain<DisruptorEvent> originalChain = new ProxiedHandlerChain();
		//执行事件处理链
		this.doHandler(event, originalChain);
	}

}

