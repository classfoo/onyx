package org.classfoo.onyx.api.model;

import java.util.List;

/**
 * Onyx Label
 * @author ClassFoo
 *
 */
public interface OnyxLabel {

    /**
     * get parent label names
     * @return
     */
    public List<String> getParents();

    /**
     * get parent label by name
     * @param name
     * @return
     */
    public OnyxLabel getParent(String name);

    /**
     * get children label names
     * @return
     */
    public List<String> getChildren();

    /**
     * get child label by name
     * @param name
     * @return
     */
    public OnyxLabel getChild(String name);

    /**
     * get label property names
     * @return
     */
    public List<String> getProperties();

    /**
     * get label property by name
     * @param property
     * @return
     */
    public OnyxProperty getProperty(String property);

}
