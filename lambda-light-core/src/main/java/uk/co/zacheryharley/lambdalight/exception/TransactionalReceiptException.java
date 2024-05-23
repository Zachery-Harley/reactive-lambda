package uk.co.zacheryharley.lambdalight.exception;

public class TransactionalReceiptException extends RuntimeException {
    public TransactionalReceiptException(String message) {
        super(message);
    }

    public TransactionalReceiptException(String message, Throwable cause) {
        super(message, cause);
    }
}
