package org.classfoo.onyx.api.query;

import java.util.List;

public interface OnyxQueryList<T> extends OnyxQuery {

	public List<T> queryList();

	public List<T> queryList(long limit);

}
