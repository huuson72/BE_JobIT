package vn.hstore.jobhunter.util.error;

public class QuotaExceededException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public QuotaExceededException(String message) {
        super(message);
    }
} 