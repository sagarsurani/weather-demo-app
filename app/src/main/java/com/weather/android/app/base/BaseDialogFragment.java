package com.weather.android.app.base;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;

/**
 * Created  on 18/08/17.
 */

public abstract class BaseDialogFragment<P extends MVPBasePresenter> extends MVPBaseDialogFragment<P> {

    /**
     * Returns screen name of current screen
     * Used in analytics
     *
     * @return
     */
    public abstract String getScreenName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

}
