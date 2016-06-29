package com.lendico.iban.exception;


/**
 * Used to wrap external exceptions inside of OrderManagement.<br>
 * @author noman
 */
public class IbanServiceException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private IbanExceptionType action;

    public IbanServiceException(IbanExceptionType action, String message, Throwable cause) {
        super(message, cause);
        this.action = action;
    }
    
    public IbanServiceException(IbanExceptionType action, String message) {
        super(message);
        this.action = action;
    }
    
    public IbanExceptionType getIbanExceptionType() {
        return action;
    }

    /**
     * Constructor to use when another exception should be wrapped.
     * @param t The exception to wrap.
     */
    public IbanServiceException(Throwable t) {
        super(t);
    }

    /**
     * Default constructor.
     */
    public IbanServiceException() {
        super();
    }

    /**
     * Constructor to use when the exception should contain additional info.
     * @param message The additional info to include.
     */
    public IbanServiceException(String message) {
        super(message);
    }

    /**
     * Constructor to wrap an exception and include additional info.
     * @param message The additional info to include.
     * @param t The exception to wrap.
     */
    public IbanServiceException(String message, Throwable t) {
        super(message, t);
    }
}
