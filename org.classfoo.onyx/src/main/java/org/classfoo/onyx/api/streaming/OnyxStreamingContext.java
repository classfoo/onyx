package org.classfoo.onyx.api.streaming;

/**
 * Onyx Streaming Context
 * @author ClassFoo
 *
 */
public interface OnyxStreamingContext {

	String putEntityIdByProperty(String property, String value, String id);

	String getEntityIdByProperty(String property, String value);

}
