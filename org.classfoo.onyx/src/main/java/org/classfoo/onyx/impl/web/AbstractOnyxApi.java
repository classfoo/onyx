package org.classfoo.onyx.impl.web;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.classfoo.onyx.api.web.OnyxApi;

/**
 * OnyxApi abstract implement
 * @see OnyxApi
 * @author ClassFoo
 *
 */
public abstract class AbstractOnyxApi implements OnyxApi {

    @Override
    public Object get(Map<String, Object> args) throws IOException {
        String resid = MapUtils.getString(args, RESID);
        if (StringUtils.isBlank(resid)) {
            return this.getList(args);
        }
        else {
            return this.getSingle(resid, args);
        }
    }

    public Object getSingle(String resid, Map<String, Object> args) throws IOException {
        return null;
    }

    public Object getList(Map<String, Object> args) throws IOException {
        return null;
    }

    @Override
    public Object head(Map<String, Object> args) throws IOException {

        return null;
    }

    @Override
    public Object options(Map<String, Object> args) throws IOException {

        return null;
    }

    @Override
    public Object patch(Map<String, Object> args) throws IOException {

        return null;
    }

    @Override
    public Object post(Map<String, Object> args) throws IOException {

        return null;
    }

    @Override
    public Map<String, Object> delete(Map<String, Object> args) throws IOException {

        return null;
    }

    @Override
    public Object trace(Map<String, Object> args) throws IOException {

        return null;
    }

    @Override
    public Object put(Map<String, Object> args) throws IOException {

        return null;
    }
}
