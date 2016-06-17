package com.kiwi.auready_ver2.rest_service;

import com.kiwi.auready_ver2.login.IBaseUrl;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kiwi on 6/16/16.
 */
public class ServiceGenerator {

    public static final String BASE_URL = IBaseUrl.BASE_URL;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder baseBuilder =
            new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    // Request Auth to access Server
    public static <S> S createService(Class<S> serviceClass) {

//        httpClient.interceptors().clear();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .build();
                Response response = chain.proceed(request);
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = baseBuilder.client(client).build();
        return retrofit.create(serviceClass);
    }


}
