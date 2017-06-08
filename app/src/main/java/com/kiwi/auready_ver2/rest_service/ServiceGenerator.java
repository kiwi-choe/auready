package com.kiwi.auready_ver2.rest_service;

import android.support.annotation.NonNull;

import com.kiwi.auready_ver2.login.IBaseUrl;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
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

    private static final int CONNECT_TIMEOUT = 15;
    private static final long WRITE_TIMEOUT = 15;
    private static final long READ_TIMEOUT = 10;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);

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

    // Basic Authentication
    public static <S> S createService(Class<S> serviceClass,
                                      @NonNull String clientId, @NonNull String clientSecret) {
        String authToken = Credentials.basic(clientId, clientSecret);
        AuthenticationInterceptor interceptor =
                new AuthenticationInterceptor(authToken);

        Retrofit retrofit = null;
        if (!httpClient.interceptors().contains(interceptor)) {
            httpClient.addInterceptor(interceptor);

            baseBuilder.client(httpClient.build());
            retrofit = baseBuilder.build();
        }
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass, @NonNull final String accessToken) {
//        checkNotNull(accessToken, "accessToken is null");
        httpClient.interceptors().clear();
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request original = chain.request();
                Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .header("Authorization", "Bearer" + " " + accessToken)
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

    private static class AuthenticationInterceptor implements Interceptor {
        private String authToken;

        AuthenticationInterceptor(String token) {
            this.authToken = token;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", authToken)
                    .method(original.method(), original.body());

            Request request = builder.build();
            return chain.proceed(request);
        }
    }
}
