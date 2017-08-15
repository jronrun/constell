package com.benayn.constell.services.capricorn.enums;

import static com.benayn.constell.service.util.Enumerates.find;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserStatus {
    USING((short) 1),
    LOCKED((short) 8),
    DELETED((short) 9)
    ;

    @Getter
    private final short value;

    public static UserStatus get(int val) {
        return find(UserStatus.class, x -> x.value == val);
    }
}
