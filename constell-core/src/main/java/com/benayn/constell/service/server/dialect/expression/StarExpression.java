package com.benayn.constell.service.server.dialect.expression;

import com.benayn.constell.service.util.LZString;
import java.util.Locale;

public class StarExpression {

    public StarExpression(Locale locale) {

    }

    public String encodes(Object target) {
        return LZString.encodes(target);
    }
}
