package org.classfoo.onyx.api.model;

import java.util.List;

/**
 * Onyx Label Property
 * @author ClassFoo
 *
 */
public interface OnyxProperty {

	/**
	 * get property type
	 * @return
	 */
	public int getType();

	/**
	 * get property labels
	 * @return
	 */
	public List<String> getLabels();

	/**
	 * get property label
	 * @param label
	 * @return
	 */
	public OnyxLabel getLabel(String label);

	/**
	 * get values
	 * @return
	 */
	public List<Object> getValues();
}
