package org.classfoo.onyx.api.streaming;

/**
 * Onyx Streaming Message
 * @author ClassFoo
 *
 */
public interface OnyxStreamingMessage {

    /**
     * get streaming name
     * @return
     */
    public String getStreaming();

    /**
     * get message body
     * @return
     */
    public Object getBody();

}
