package myour.myourforum.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTfulAPIConfig {
    public static final String URL = "http://192.168.1.2:8083";

    public static Retrofit getClient(String url) {
        Gson gson = new GsonBuilder().setLenient().create();

        OkHttpClient clientBuilder = new OkHttpClient.Builder()
                .readTimeout(5000, TimeUnit.MILLISECONDS)
                .writeTimeout(5000, TimeUnit.MILLISECONDS)
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(true)
                .build();

        return new Retrofit.Builder()
                .baseUrl(url)
                .client(clientBuilder)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static RESTfulAPIRequest getServer() {
        return RESTfulAPIConfig.getClient(RESTfulAPIConfig.URL).create(RESTfulAPIRequest.class);
    }
}
