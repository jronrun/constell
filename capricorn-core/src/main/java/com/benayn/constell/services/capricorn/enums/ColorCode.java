package com.benayn.constell.services.capricorn.enums;

import static com.benayn.constell.service.util.Enumerates.find;

import com.benayn.constell.service.server.respond.OptionValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum  ColorCode implements OptionValue<String> {
    RED("red",          "enum.color.code.red"),
    ORANGE("orange",    "enum.color.code.orange"),
    YELLOW("yellow",    "enum.color.code.yellow"),
    OLIVE("olive",      "enum.color.code.olive"),
    GREEN("green",      "enum.color.code.green"),
    TEAL("teal",        "enum.color.code.teal"),
    BLUE("blue",        "enum.color.code.blue"),
    VIOLET("violet",    "enum.color.code.violet"),
    PURPLE("purple",    "enum.color.code.purple"),
    PINK("pink",        "enum.color.code.pink"),
    BROWN("brown",      "enum.color.code.brown"),
    GREY("grey",        "enum.color.code.grey"),
    BLACK("black",      "enum.color.code.black"),
    ;

    private final String value;
    private final String label;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public static ColorCode get(String val) {
        return find(ColorCode.class, x -> x.value.equals(val));
    }

}
