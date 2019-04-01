package com.yourtrack.dataservice;

public class YTException extends Exception {
    public YTException(String message) {
        super(message);
    }

    public YTException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public YTException(String message, Throwable cause) {
        super(message, cause);
    }
}
