package org.classfoo.onyx.api.web;

import java.util.Map;

/**
 * Onyx Api Interface
 * @author ClassFoo
 *
 */
public interface OnyxApi {

	String VERSION = "version";

	String RESOURCE = "resource";

	String RESID = "resid";

	String LOGIN = "login";

	String REQUEST = "request";

	String RESPONSE = "response";

	String getResource();

	public Object delete(Map<String, Object> args) throws Exception;

	public Object get(Map<String, Object> args) throws Exception;

	public Object head(Map<String, Object> args) throws Exception;

	public Object options(Map<String, Object> args) throws Exception;

	public Object patch(Map<String, Object> args) throws Exception;

	public Object post(Map<String, Object> args) throws Exception;

	public Object trace(Map<String, Object> args) throws Exception;

	public Object put(Map<String, Object> args) throws Exception;

}
