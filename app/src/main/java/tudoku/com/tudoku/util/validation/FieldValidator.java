package tudoku.com.tudoku.util.validation;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import tudoku.com.tudoku.R;

public class FieldValidator {

    List<ValidationTarget> mTargets;
    Context mContext;

    public FieldValidator(Context context) {
        mContext = context;
        mTargets = new ArrayList<ValidationTarget>();
    }

    public void addForValidation(View targetView, ValidationType validationType) {
        mTargets.add(new ValidationTarget(targetView, validationType));
    }

    public ValidationResult validate() {
        for (ValidationTarget target : mTargets) {
            EditText field = (EditText) target.getView();
            switch (target.getValidationType()) {
                case USERNAME:
                    if (TextUtils.isEmpty(field.getText())) {
                        return new ValidationResult(field, mContext.getString(R.string.error_username_empty));
                    }
                    break;
                case PASSWORD:
                    if (TextUtils.isEmpty(field.getText())) {
                        return new ValidationResult(field, mContext.getString(R.string.error_password_empty));
                    }
                    break;
            }
        }
        return ValidationResult.RESULT_OK;
    }
}
