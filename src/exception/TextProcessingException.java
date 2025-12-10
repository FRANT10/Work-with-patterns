package exception;

public class TextProcessingException  extends Exception{
    public TextProcessingException() {
        super();
    }

    public TextProcessingException(String message) {
        super(message);
    }

    public TextProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public TextProcessingException(Throwable cause) {
        super(cause);
    }

    protected TextProcessingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
