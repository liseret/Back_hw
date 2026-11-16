package app;


public class ApiException extends RuntimeException {
    public final int status;
    public final String code;

    public final String allow;

    public ApiException(int status, String code, String message) {
        this(status, code, message, null);
    }

    public ApiException(int status, String code, String message, String allow) {
        super(message);
        this.status = status;
        this.code = code;
        this.allow = allow;
    }
}
