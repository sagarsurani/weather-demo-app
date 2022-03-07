package com.weather.android.app.base;

/**
 * Created on 10/09/16.
 */
public interface MVPBaseView<P> {

    /**
     * returns whether view is accepting requests to update ui
     *
     * @return
     */
    boolean isAlive();

    P getNewPresenter();

}