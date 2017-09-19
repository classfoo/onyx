package org.classfoo.onyx.api.operate;

import org.classfoo.onyx.api.OnyxService;

/**
 * Onyx Batch Operate
 * @author ClassFoo
 *
 */
public interface OnyxOperateBatch extends OnyxOperate {

	/**
	 * Add Operate to Batch
	 * @param operate
	 * @return
	 */
	public OnyxOperateBatch addOperate(OnyxOperate operate);

	/**
	 * Create Operate and add to Batch
	 * @param onyxService
	 * @param type
	 * @return
	 */
	public <T extends OnyxOperate> T createOperate(OnyxService onyxService, Class<T> type);

}
