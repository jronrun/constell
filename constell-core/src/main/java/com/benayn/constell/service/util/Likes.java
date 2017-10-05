package com.benayn.constell.service.util;

import java.text.MessageFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class Likes {

    public static String get(String target) {
        return get(target, Side.BOTH);
    }

    public static String get(String target, Side side) {
        return MessageFormat.format(side.getLike(), target);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Side {
        LEFT("%{0}"), RIGHT("{0}%"), BOTH("%{0}%");

        @Getter
        private final String like;
    }

}
