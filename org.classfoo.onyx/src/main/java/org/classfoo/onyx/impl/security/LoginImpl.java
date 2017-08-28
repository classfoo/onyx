package org.classfoo.onyx.impl.security;

import org.classfoo.onyx.api.security.Login;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
public class LoginImpl implements Login {

}
