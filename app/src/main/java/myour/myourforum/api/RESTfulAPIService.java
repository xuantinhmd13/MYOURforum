package myour.myourforum.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import myour.myourforum.model.Category;
import myour.myourforum.model.Post;
import myour.myourforum.model.User;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RESTfulAPIService {
    //CONFIG
    String URL = "http://192.168.1.5:8083";

    Gson gson = new GsonBuilder().setLenient().create();

    OkHttpClient clientBuilder = new OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .writeTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build();

    RESTfulAPIService request = new Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(clientBuilder)
            .build()
            .create(RESTfulAPIService.class);

    //REQUEST METHOD
    @GET("/hello")
    Call<String> testAPI();

    //--------------------------------------CATEGORY.
    @GET("/categories")
    Call<List<Category>> getAllCategory();

    //--------------------------------------POST.
    @GET("/posts/by-category-id")
    Call<List<Post>> getPostByCategory(@Query("categoryId") int categoryId, @Query("pageIndexMain") int pageIndexMain, @Query("size") int size);

    @GET("/posts/search")
    Call<List<Post>> searchPost(@Query("keyWord") String keyWord, @Query("categoryId") int categoryId,
                                @Query("pageIndexSearch") int pageIndexSearch, @Query("size") int size);

    @GET("/posts/{id}")
    Call<Post> getPostById(@Path("id") int id);

    @POST("/posts")
    Call<Integer> addPost(@Body Post newPost);

    @PUT("/posts/{id}")
    Call<Void> updatePost(@Body Post postEdited);

    @DELETE("/posts/{id}")
    Call<Void> deletePost(@Query("id") int id);

    //--------------------------------------USER.
    @GET("/users/{id}")
    Call<User> getUserById(@Query("id") int id);

    @POST("/users/register")
    Call<String> register(@Body User user);

    @POST("/users/login")
    Call<User> login(@Query("email") String email);

    @PUT("/posts/{id}/view-count")
    Call<Integer> increaseViewCount(@Query("id") int id);

    @PUT("/users/{id}")
    Call<User> updateUser(@Body User userEdited);

    @PUT("/users/{id}/password")
    Call<Void> updatePassword(@Query("id") int id, @Query("passwordNew") String passwordNew, @Query("updateTime") String updateTime);

    //--------------------------------------IMAGE.
    @POST("/image/post/{id}")
    @Multipart
    Call<Void> uploadImagePost(@Part MultipartBody.Part image, @Query("id") int id);
}
