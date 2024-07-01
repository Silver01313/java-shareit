package ru.practicum.shareit.booking;

import java.util.HashMap;
import java.util.Map;

public enum BookingArgument {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    private static final Map<String, BookingArgument> ARGUMENT_STRING_MAP = new HashMap<>();

    static {
        for (BookingArgument param : values()) {
            ARGUMENT_STRING_MAP.put(param.name(), param);
        }
    }

    public static BookingArgument getParam(String param) {
        if (ARGUMENT_STRING_MAP.containsKey(param)) {
            return ARGUMENT_STRING_MAP.get(param);
        }
        return null;
    }
}
