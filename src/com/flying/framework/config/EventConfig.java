package com.flying.framework.config;

import java.util.List;

import com.flying.common.util.Utils;

/**
 * @author wanghaifeng
 *
 */
public class EventConfig extends BaseConfig {
	private static final long serialVersionUID = -4048399275719562420L;
	public static final String EVENT_CONFIG_KEY = "EVENT_CONFIG_KEY";
	private final String sender;
	private final List<EventHandler> handlers = Utils.newArrayList();

	public EventConfig(String id, String sender) {
		super(id);
		this.sender = sender;
	}

	public List<EventHandler> getHandlers() {
		return handlers;
	}

	public String getSender() {
		return sender;
	}

	public String toString() {
		return sender + this.handlers;
	}
}
