package com.weather.android.app.data.local.prefs

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.weather.android.app.data.models.RecentSearchHistory


/**
 * Created by .
 */

class PreferenceDataHelper private constructor(context: Context) {
    private val mSharedPreferenceHelper: SharedPreferenceHelper

    companion object {
        private var sInstance: PreferenceDataHelper? = null

        @Synchronized
        fun getInstance(context: Context): PreferenceDataHelper? {
            if (sInstance == null) {
                sInstance = PreferenceDataHelper(context)
            }
            return sInstance
        }
    }

    init {
        SharedPreferenceHelper.initialize(context)
        mSharedPreferenceHelper = SharedPreferenceHelper.getsInstance()!!
    }

    fun firstTimeAskingPermission(permission: String, isFirstTime: Boolean) {
        mSharedPreferenceHelper.setBoolean(permission, isFirstTime)
    }

    fun isFirstTimeAskingPermission(permission: String): Boolean {
        return mSharedPreferenceHelper.getBoolean(permission, true)
    }

    private fun saveRecentSearchList(articleItems: ArrayList<RecentSearchHistory>) {
        mSharedPreferenceHelper.setString("aa", Gson().toJson(articleItems))
    }

    fun getRecentSearchList(): ArrayList<RecentSearchHistory> {
        val json: String = mSharedPreferenceHelper.getString("aa", "")!!
        var articles: ArrayList<RecentSearchHistory> = ArrayList()
        if (!TextUtils.isEmpty(json)) {
            articles = Gson().fromJson(
                json,
                object : TypeToken<ArrayList<RecentSearchHistory?>?>() {}.type
            )
        }
        return articles
    }

    fun deleteSearchItem(pos: Int) {
        val bookmarkArticles: ArrayList<RecentSearchHistory> = getRecentSearchList()
        for (i in 0 until bookmarkArticles.size) {
            if (pos == i) {
                bookmarkArticles.removeAt(pos)
                break
            }
        }
        saveRecentSearchList(bookmarkArticles)
    }

    fun addSearchItem(recentSearchItem: RecentSearchHistory) {
        val list: ArrayList<RecentSearchHistory> = getRecentSearchList()
        list.add(0, recentSearchItem)
        saveRecentSearchList(list)
    }
}