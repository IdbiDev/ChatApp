package me.idbi.chatapp.utils;

import lombok.AllArgsConstructor;

import java.math.BigInteger;
import java.util.regex.Pattern;

@AllArgsConstructor
public enum StringPatterns {

    NONE(null),
    NAME(Pattern.compile("^[\\w]{0,16}$")),
    NUMBER(Pattern.compile("^[0-9]+$")),
    PASSWORD(Pattern.compile("^[!-~]{0,16}$"));

    private final Pattern pattern;

    public boolean match(String text) {
        if(this == NONE) return true;
        if(this == NUMBER && pattern.matcher(text).matches()) {
            try {
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
        return pattern.matcher(text).matches();
    }
}
