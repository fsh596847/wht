package com.xiaowei.android.wht.ui.doctorzone.net;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 275813 Created by johnwatson on 4/19/16.
 */
public class RestClient {

  //private static final String BASE_URL ="http://192.168.1.225/";
  protected static final long REQUEST_TIMEOUT = 20;
  private static final String BASE_URL = "http://upload.sxyygh.com:8015/";

  public static <S> S createService(Class<S> serviceClass) {
    return setupRestClient(serviceClass);
  }

  private static <S> S setupRestClient(Class<S> serviceClass) {
    //HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    //// set your desired log level
    //if (BuildConfig.RELEASED) {
    //  logging.setLevel(HttpLoggingInterceptor.Level.NONE);
    //} else {
    //  logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    //}
    //ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
    //    //.tlsVersions(TlsVersion.TLS_1_0)
    //    .cipherSuites(CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
    //        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256).build();

    OkHttpClient.Builder builder =
        new OkHttpClient.Builder().connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
    //.connectionSpecs(Collections.singletonList(spec));
    //.certificatePinner(new CertificatePinner.Builder().add("testpay.sxyygh.com",
    //    "sha256/cuXvZRlKVYX5hY+jpx9FrJ1pjcPazYjvNxSBb1P6uUQ=").build())

    builder.addNetworkInterceptor(new StethoInterceptor());
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    // set your desired log level
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    builder.addInterceptor(logging);

    OkHttpClient client = builder.build();

    Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
        .client(client)
        //.addConverterFactory(LoganSquareConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();

    //sPayService = retrofit.create(serviceClass);

    return retrofit.create(serviceClass);
  }
}