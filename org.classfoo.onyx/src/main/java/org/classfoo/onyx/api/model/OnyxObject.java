package org.classfoo.onyx.api.model;

import java.util.List;

/**
 * Onyx Object
 * @author ClassFoo
 *
 */
public interface OnyxObject {

	/**
	 * get label names
	 * @return
	 */
	public List<String> getLabels();

	/**
	 * get label by name
	 * @param name
	 * @return
	 */
	public OnyxLabel getLabel(String name);

	/**
	 * get property names
	 * @return
	 */
	public List<String> getProperties();

	/**
	 * get property by name
	 * @return
	 */
	public OnyxProperty getProperty();

	/**
	 * get object threads
	 * @return
	 */
	public List<String> getThreads();

	/**
	 * get object thread start period
	 * @param thread
	 * @return
	 */
	public Object getThreadStart(String thread);

	/**
	 * get object thread end period
	 * @param thread
	 * @return
	 */
	public Object getThreadEnd(String thread);

	/**
	 * get object thread next objects
	 * @param thread
	 * @return
	 */
	public List<String> getThreadNexts(String thread);

	/**
	 * get object thread previous objects
	 * @param thread
	 * @return
	 */
	public List<String> getThreadPrevious(String thread);

	/**
	 * get object thread sibling objects
	 * @param thread
	 * @return
	 */
	public List<String> getThreadSiblings(String thread);
}
