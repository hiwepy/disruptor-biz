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

import com.lmax.disruptor.biz.event.DisruptorEvent;
import com.lmax.disruptor.biz.event.handler.chain.HandlerChain;

/*
 * 实现EventHandler是为了作为BatchEventProcessor的事件处理器，
 * 实现WorkHandler是为了作为WorkerPool的事件处理器
 */
public interface DisruptorHandler<T extends DisruptorEvent> {

	public void doHandler(T event, HandlerChain<T> handlerChain) throws Exception;
	
}
