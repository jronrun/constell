package com.benayn.constell.services.capricorn.enums;

import static com.benayn.constell.service.util.Enumerates.find;

import com.benayn.constell.service.server.respond.OptionValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AccountStatus implements OptionValue<Short> {
    USING((short) 1, "enum.account.status.using"),
    DELETED((short) 9, "enum.account.status.deleted")
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

    public static AccountStatus get(int val) {
        return find(AccountStatus.class, x -> x.value == val);
    }
}
