package com.weather.android.app.base;

/**
 * Created on 27/10/16.
 */

/**
 * BaseFragment for all fragments
 * - Has all common functionalites needed in all fragments
 */

public abstract class BaseFragment<P extends MVPBasePresenter> extends MVPBaseFragment<P> {

    /**
     * Returns screen name of current screen
     * Used in analytics
     *
     * @return
     */
    public abstract String getScreenName();

}
