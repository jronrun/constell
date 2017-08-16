package com.benayn.constell.service.enums;

import static com.benayn.constell.service.util.Enumerates.find;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Gender {
    MALE((short) 1),
    FEMALE((short) 2),
    UNKNOWN((short) 3)
    ;

    @Getter
    private final short value;

    public static Gender get(int val) {
        return find(Gender.class, x -> x.value == val);
    }

}
