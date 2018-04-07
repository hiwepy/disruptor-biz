/*
 * Copyright (c) 2018 (https://github.com/vindell).
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

import java.util.Map;
import java.util.Set;

import com.lmax.disruptor.biz.event.DisruptorEvent;
import com.lmax.disruptor.biz.event.handler.DisruptorHandler;
import com.lmax.disruptor.biz.event.handler.NamedHandlerList;

/**
 * HandlerChain管理器，负责创建和维护HandlerChain
 */
public interface HandlerChainManager<T extends DisruptorEvent> {

	/**
	 * 获取所有HandlerChain
	 * @return
	 */
    Map<String, DisruptorHandler<T>> getHandlers();

    /**
     * 根据指定的chainName获取Handler列表
     */
    NamedHandlerList<T> getChain(String chainName);

    /**
     * 是否有HandlerChain
     */
    boolean hasChains();

    /**
     * 获取HandlerChain名称列表
     */
    Set<String> getChainNames();

    /**
     * <p>生成代理HandlerChain,先执行chainName指定的filerChian,最后执行servlet容器的original<p>
     */
    HandlerChain<T> proxy(HandlerChain<T> original, String chainName);

   /**
    * 
    * <p>方法说明：增加handler到handler列表中<p>
    */
    void addHandler(String name, DisruptorHandler<T> handler);
    
    /**
     * <p>方法说明：创建HandlerChain<p>
     */
    void createChain(String chainName, String chainDefinition);

    /**
     * <p>方法说明：追加handler到指定的HandlerChian中<p>
     */
    void addToChain(String chainName, String handlerName);
	
}