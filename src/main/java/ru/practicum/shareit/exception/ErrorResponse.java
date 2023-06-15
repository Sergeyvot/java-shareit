package ru.practicum.shareit.exception;

public class ErrorResponse {
    public String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
