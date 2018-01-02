package org.classfoo.onyx.impl.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Onyx Pages
 * @author ClassFoo
 *
 */
@Controller
public class ActionOnyx {

    @RequestMapping(path = { "/", "/space/**", "/base/**", "/entity/**", "/personal/**", "/settings/**", "/material/**",
            "/edit/**", "/view/**", "/graph/**", "/search/**" })
    public String execute(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "index";
    }
}
