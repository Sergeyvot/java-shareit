package ru.practicum.shareit.booking;

public enum State {
    WAITING, REJECTED, ALL, CURRENT, FUTURE, PAST, UNKNOW;

    public static State getEnum(String state) {

        switch (state) {
            case "WAITING":
                return WAITING;
            case "REJECTED":
                return REJECTED;
            case "ALL":
                return ALL;
            case "CURRENT":
                return CURRENT;
            case "FUTURE":
                return FUTURE;
            case "PAST":
                return PAST;
            default:
                return UNKNOW;
        }
    }
}
