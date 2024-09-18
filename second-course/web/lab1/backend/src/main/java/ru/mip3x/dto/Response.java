package ru.mip3x.dto;

public record Response (
        int statusCode,
        String statusMessage,
        ValidatedData validatedData,
        Boolean result,
        Long executionTime
) {
    public Response(int statusCode, String statusMessage) {
        this(statusCode, statusMessage, null, null, null);
    }
}
