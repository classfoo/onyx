package org.classfoo.onyx.impl.streaming;

import org.classfoo.onyx.api.streaming.OnyxStreamingMessage;

/**
 * @see OnyxStreamingMessage
 * @author ClassFoo
 *
 */
public class OnyxStreamingMessageImpl implements OnyxStreamingMessage {

	private String streaming;

	private Object message;

	public OnyxStreamingMessageImpl(String streaming, Object message) {
		this.streaming = streaming;
		this.message = message;
	}

	@Override
	public String getStreaming() {
		return this.streaming;
	}

	@Override
	public Object getBody() {
		return this.message;
	}

	@Override
	public String toString() {
		return this.streaming;
	}
}
