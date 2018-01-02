package org.classfoo.onyx.impl.security;

import org.classfoo.onyx.api.security.Login;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * @see Login
 * @author ClassFoo
 * @createdate 20180102
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
public class LoginImpl implements Login {

}
