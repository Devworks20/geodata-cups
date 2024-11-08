package com.geodata.cups.Backend.Retrofit.Host;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient
{
    public static int MY_SOCKET_TIMEOUT_MINUTE = 1; //VOLLEY TIME SOCKET - 1 MINUTE

    public static String URL_API = "https://demo.geosolutions.com.ph:8001/";

   // public static String URL_API_TEST = "https://demo.geosolutions.com.ph:8002/";

    public static Retrofit getClient()
    {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(MY_SOCKET_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .writeTimeout(MY_SOCKET_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .readTimeout(MY_SOCKET_TIMEOUT_MINUTE, TimeUnit.MINUTES)
                .addInterceptor(interceptor).build();

        return new Retrofit.Builder()
                .baseUrl(URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

}
