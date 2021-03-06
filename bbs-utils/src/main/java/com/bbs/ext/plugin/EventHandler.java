package com.bbs.ext.plugin;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import com.google.common.collect.Multimap;
import com.bbs.ext.plugin.core.ApplicationEvent;
import com.bbs.ext.plugin.core.ApplicationListener;

/**
 * 事件实际处理的类
 * @author L.cm
 * email: 596392912@qq.com
 * site:http://www.dreamlu.net
 * date 2015年4月26日下午10:02:46
 */
@SuppressWarnings("rawtypes")
class EventHandler {

	private final Multimap<Type, ApplicationListener> map;
	private final ExecutorService pool;

	public EventHandler(Multimap<Type, ApplicationListener> map,
			ExecutorService pool) {
		super();
		this.map = map;
		this.pool = pool;
	}

	/**
	 * 执行发送消息
	 * @param event ApplicationEvent
	 */
	@SuppressWarnings("unchecked")
	public void postEvent(final ApplicationEvent event) {
		Collection<ApplicationListener> listenerList = map.get(event.getClass());
		for (final ApplicationListener listener : listenerList) {
			if (null != pool) {
				pool.execute(new Runnable() {

					@Override
					public void run() {
						listener.onApplicationEvent(event);
					}
				});
			} else {
				listener.onApplicationEvent(event);
			}
		}
	}
}
