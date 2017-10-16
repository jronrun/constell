package com.benayn.constell.service.enums;

import static com.benayn.constell.service.util.Enumerates.find;

import com.benayn.constell.service.server.respond.OptionValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Gender implements OptionValue<Short> {
    MALE((short) 1, "enum.gender.male"),
    FEMALE((short) 2, "enum.gender.female"),
    UNKNOWN((short) 3, "enum.unknown")
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

    public static Gender get(int val) {
        return find(Gender.class, x -> x.value == val);
    }

}
