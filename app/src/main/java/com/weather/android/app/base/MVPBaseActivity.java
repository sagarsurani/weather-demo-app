package com.weather.android.app.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created on 17/10/16.
 */

public abstract class MVPBaseActivity<P extends MVPBasePresenter> extends AppCompatActivity implements MVPBaseView<P> {

    boolean isDestroyed;
    private P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = getNewPresenter();
        getPresenter().bindView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed = true;
        getPresenter().unBindView();
    }

    @Override
    public boolean isAlive() {
        return !isFinishing() && !isDestroyed;
    }

    public final P getPresenter() {
        return presenter;
    }

}
