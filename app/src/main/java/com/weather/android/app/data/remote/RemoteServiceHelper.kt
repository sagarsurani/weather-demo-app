package com.weather.android.app.data.remote

import android.content.Context
import android.text.TextUtils
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.weather.android.app.BuildConfig
import com.weather.android.app.data.local.prefs.PreferenceDataHelper
import com.weather.android.app.utils.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by
 */

object RemoteServiceHelper {
    private var instance: Retrofit? = null

    /**
     * Creates new unique retrofit instance if not created already
     *
     * @param context
     * @return
     */

    @Synchronized
    fun getRetrofitInstance(context: Context): Retrofit? {
        if (instance == null) {
            instance = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClientBuilder(context, false).build())
                .build()
        }
        return instance
    }

    @Synchronized
    fun clearRetrofitInstance() {
        instance = null
    }

    private fun getHttpClientBuilder(
        context: Context,
        isIGInstance: Boolean
    ): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.NONE
        }
        val httpClientBuilder = OkHttpClient.Builder()
        httpClientBuilder.connectTimeout(30, TimeUnit.SECONDS)
        httpClientBuilder.readTimeout(30, TimeUnit.SECONDS)
        httpClientBuilder.writeTimeout(30, TimeUnit.SECONDS)
        if (!isIGInstance)
            httpClientBuilder.addInterceptor(
                HeaderInterceptor(
                    BaseHeaders(
                        PreferenceDataHelper.getInstance(
                            context
                        )
                    )
                )
            )
        httpClientBuilder.addInterceptor(logging)
        if (BuildConfig.DEBUG) {
            httpClientBuilder.addNetworkInterceptor(StethoInterceptor())
        }
        return httpClientBuilder
    }


    /**
     * Creates service to make network requests to endpoints
     *
     * @param apiService
     * @param retrofit
     * @param <S>
     * @return
    </S> */
    fun <S> createService(apiService: Class<S>?, retrofit: Retrofit): S {
        return retrofit.create(apiService)
    }

    @Synchronized
    fun destroyInstance() {
        instance = null
    }

    /**
     * Stores common Headers needed in each request
     */
    class BaseHeaders(private val preferenceDataHelper: PreferenceDataHelper?) {
        fun getAccessToken(): String {
            return ""
        }

    }

    internal class HeaderInterceptor(private var baseHeaders: BaseHeaders) : Interceptor {

        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            if (!TextUtils.isEmpty(baseHeaders.getAccessToken())
                && TextUtils.isEmpty(original.header(Constants.AUTHORIZATION))
            ) {
                requestBuilder.addHeader(
                    Constants.AUTHORIZATION, baseHeaders.getAccessToken()
                )
            }
            val request = requestBuilder.build()
            return chain.proceed(request)
        }

    }

}