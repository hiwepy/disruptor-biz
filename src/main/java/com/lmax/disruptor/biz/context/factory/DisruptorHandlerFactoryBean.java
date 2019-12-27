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
package com.lmax.disruptor.biz.context.factory;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.CollectionUtils;

import com.lmax.disruptor.biz.config.Ini;
import com.lmax.disruptor.biz.event.DisruptorEvent;
import com.lmax.disruptor.biz.event.handler.AbstractRouteableEventHandler;
import com.lmax.disruptor.biz.event.handler.DisruptorEventHandler;
import com.lmax.disruptor.biz.event.handler.DisruptorHandler;
import com.lmax.disruptor.biz.event.handler.Nameable;
import com.lmax.disruptor.biz.event.handler.chain.HandlerChainManager;
import com.lmax.disruptor.biz.event.handler.chain.def.DefaultHandlerChainManager;
import com.lmax.disruptor.biz.event.handler.chain.def.PathMatchingHandlerChainResolver;

public class DisruptorHandlerFactoryBean implements FactoryBean<DisruptorHandler<DisruptorEvent>> {

	/**
	 * 处理器定义
	 */
	private Map<String, DisruptorHandler<DisruptorEvent>> handlers;
	
	/**
	 * 处理器链定义
	 */
	private Map<String, String> handlerChainDefinitionMap;
	
	private AbstractRouteableEventHandler<DisruptorEvent> instance;

	public DisruptorHandlerFactoryBean() {
		handlers = new LinkedHashMap<String, DisruptorHandler<DisruptorEvent>>();
		handlerChainDefinitionMap = new LinkedHashMap<String, String>();
	}

	@Override
	public DisruptorHandler<DisruptorEvent> getObject() throws Exception {
		if(instance == null){
			instance = createInstance();
		}
		return instance;
	}

	@Override
	public Class<?> getObjectType() {
		return DisruptorEventHandler.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}
	
	public Map<String, String> getHandlerChainDefinitionMap() {
		return handlerChainDefinitionMap;
	}

	public void setHandlerChainDefinitionMap(Map<String, String> handlerChainDefinitionMap) {
		this.handlerChainDefinitionMap = handlerChainDefinitionMap;
	}

	public Map<String, DisruptorHandler<DisruptorEvent>> getHandlers() {
		return handlers;
	}

	public void setHandlers(Map<String, DisruptorHandler<DisruptorEvent>> handlers) {
		this.handlers = handlers;
	}
	
	public void setHandlerChainDefinitions(String definitions) {
        Ini ini = new Ini();
        ini.load(definitions);
        Ini.Section section = ini.getSection("urls");
        if (CollectionUtils.isEmpty(section)) {
            section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
        }
        setHandlerChainDefinitionMap(section);
    }
	
	protected HandlerChainManager<DisruptorEvent> createHandlerChainManager() {

		HandlerChainManager<DisruptorEvent> manager = new DefaultHandlerChainManager();
		Map<String, DisruptorHandler<DisruptorEvent>> handlers = getHandlers();
		if (!CollectionUtils.isEmpty(handlers)) {
			for (Map.Entry<String, DisruptorHandler<DisruptorEvent>> entry : handlers.entrySet()) {
				String name = entry.getKey();
				DisruptorHandler<DisruptorEvent> handler = entry.getValue();
				if (handler instanceof Nameable) {
					((Nameable) handler).setName(name);
				}
				manager.addHandler(name, handler);
			}
		}

		Map<String, String> chains = getHandlerChainDefinitionMap();
		if (!CollectionUtils.isEmpty(chains)) {
			for (Map.Entry<String, String> entry : chains.entrySet()) {
				// topic/tags/keys
				String url = entry.getKey();
				String chainDefinition = entry.getValue();
				manager.createChain(url, chainDefinition);
			}
		}

		return manager;
	}
	
	protected AbstractRouteableEventHandler<DisruptorEvent> createInstance() throws Exception {
		HandlerChainManager<DisruptorEvent> manager = createHandlerChainManager();
        PathMatchingHandlerChainResolver chainResolver = new PathMatchingHandlerChainResolver();
        chainResolver.setHandlerChainManager(manager);
        return new DisruptorEventHandler(chainResolver);
    }
	
	
	
}
