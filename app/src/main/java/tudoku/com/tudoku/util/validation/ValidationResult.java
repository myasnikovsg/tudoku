package tudoku.com.tudoku.util.validation;

import android.view.View;

public class ValidationResult {

    public static final ValidationResult RESULT_OK = new ValidationResult(null, "OK");

    private View mReason;
    private String mMessage;

    public ValidationResult(View reason, String message) {
        mReason = reason;
        mMessage = message;
    }

    public View getReason() {
        return mReason;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ValidationResult)) {
            return false;
        }
        ValidationResult validationResult = (ValidationResult) o;
        // special case for RESULT_OK
        if (mReason == null) {
            return validationResult.getReason() == null &&
                    mMessage.equals(validationResult.getMessage());
        }
        return mReason.equals(validationResult.getReason()) &&
                mMessage.equals(validationResult.getMessage());
    }
}
