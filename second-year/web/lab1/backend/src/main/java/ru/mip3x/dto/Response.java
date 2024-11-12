package ru.mip3x.dto;

public record Response (
        int statusCode,
        String statusMessage,
        RequestData data,
        boolean result
) {
    public Response(int statusCode, String statusMessage) {
        this(statusCode, statusMessage, null, false);
    }
}
