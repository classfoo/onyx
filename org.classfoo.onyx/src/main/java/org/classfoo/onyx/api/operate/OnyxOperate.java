package org.classfoo.onyx.api.operate;

import java.util.Map;

import org.classfoo.onyx.api.storage.OnyxStorageSession;

/**
 * Onyx Operte
 * @author ClassFoo
 *
 */
public interface OnyxOperate {

	/**
	 * commit the operate to storage
	 * @return
	 */
	public Map<String, Object> commit();

}
