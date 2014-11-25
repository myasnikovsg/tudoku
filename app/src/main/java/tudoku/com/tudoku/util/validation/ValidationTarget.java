package tudoku.com.tudoku.util.validation;

import android.view.View;

/**
 * Created by Hedin on 13-Nov-14.
 */
public class ValidationTarget {

    private View mView;
    private ValidationType mValidationType;

    public ValidationTarget(View targetView, ValidationType validationType) {
        mView = targetView;
        mValidationType = validationType;
    }

    public View getView() {
        return mView;
    }

    public ValidationType getValidationType() {
        return mValidationType;
    }

}
