package org.fb;

/**
 * Graph API Error.
 * 
 * @author Song Qian
 * 
 */
public class GraphAPIError extends Throwable {

    private static final long serialVersionUID = 1L;

    private String errorType;
    private int errorCode;

    public GraphAPIError(String message) {
        super(message);
    }

    public GraphAPIError(String message, String errorType, int errorCode) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    public String getErrorType() {
        return this.errorType;
    }

    public int getErrorCode() {
        return this.errorCode;
    }
}
