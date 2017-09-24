package org.classfoo.onyx.impl.streaming;

import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;

public class OnyxStreamingMessageImpl implements OnyxStreamingMessage {

	private Object message;

	public OnyxStreamingMessageImpl(Object message) {
		this.message = message;
	}

	@Override
	public Object getBody() {
		return this.message;
	}

}
