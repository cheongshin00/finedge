package com.finedge.finedgeapi.exception;

import java.time.LocalDateTime;

public class ApiErrorResponse {
    private LocalDateTime localDateTime;
    private int status;
    private String errorCode;
    private String message;
    private String path;

    public ApiErrorResponse(LocalDateTime localDateTime, int status, String errorCode, String message, String path) {
        this.localDateTime = localDateTime;
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
