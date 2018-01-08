package com.benayn.constell.services.capricorn.enums;

import static com.benayn.constell.service.util.Enumerates.find;

import com.benayn.constell.service.server.respond.OptionValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ShareAuthority implements OptionValue<Short> {
    READONLY((short) 1, "enum.share.authority.readonly"),
    USER((short) 2, "enum.share.authority.user"),
    EXECUTABLE((short) 3, "enum.share.authority.executable"),
    ;

    private final Short value;
    private final String label;

    @Override
    public Short getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public static ShareAuthority get(int val) {
        return find(ShareAuthority.class, x -> x.value == val);
    }
}

