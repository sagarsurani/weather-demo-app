package com.weather.android.app.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Created  on 18/08/17.
 */

public abstract class MVPBaseDialogFragment<P extends MVPBasePresenter> extends DialogFragment implements MVPBaseView<P> {

    private P presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = getNewPresenter();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPresenter().bindView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPresenter().unBindView();
    }

    @Override
    public boolean isAlive() {
        return getActivity() != null
                && isAdded();
    }

    public final P getPresenter() {
        return presenter;
    }

}
