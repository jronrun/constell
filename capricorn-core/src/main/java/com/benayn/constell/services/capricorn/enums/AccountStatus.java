package com.benayn.constell.services.capricorn.enums;

import static com.benayn.constell.service.util.Enumerates.find;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AccountStatus {
    USING((short) 1),
    DELETED((short) 9)
    ;

    @Getter
    private final short value;

    public static AccountStatus get(int val) {
        return find(AccountStatus.class, x -> x.value == val);
    }
}
