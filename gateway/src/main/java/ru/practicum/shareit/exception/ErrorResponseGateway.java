package ru.practicum.shareit.exception;

public class ErrorResponseGateway {
    public String error;

    public ErrorResponseGateway(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
