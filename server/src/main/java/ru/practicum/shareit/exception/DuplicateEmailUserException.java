package ru.practicum.shareit.exception;

public class DuplicateEmailUserException extends RuntimeException {

    public DuplicateEmailUserException(String s) {
        super(s);
    }
}
