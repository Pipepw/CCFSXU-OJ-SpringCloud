package org.verwandlung.voj.web.util;

import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class MyLocaleResolver extends AcceptHeaderLocaleResolver {
    private Locale myLocal;
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        return myLocal==null?request.getLocale():myLocal;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        myLocal = locale;
    }
}
