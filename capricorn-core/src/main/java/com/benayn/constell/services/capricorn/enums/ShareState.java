package com.benayn.constell.services.capricorn.enums;

import static com.benayn.constell.service.util.Enumerates.find;

import com.benayn.constell.service.server.respond.OptionValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ShareState implements OptionValue<Short> {
    SHARING((short) 1, "enum.share.state.sharing"),
    STOPPED((short) 2, "enum.share.state.stopped"),
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

    public static ShareState get(int val) {
        return find(ShareState.class, x -> x.value == val);
    }
}
