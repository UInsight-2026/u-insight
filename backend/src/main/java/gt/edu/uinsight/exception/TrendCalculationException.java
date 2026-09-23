package gt.edu.uinsight.exception;

public class TrendCalculationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TrendCalculationException() {
		super();
	}

	public TrendCalculationException(String message) {
		super(message);
	}

	public TrendCalculationException(String message, Throwable cause) {
		super(message, cause);
	}

	public TrendCalculationException(Throwable cause) {
		super(cause);
	}
}
