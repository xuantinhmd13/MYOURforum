package myour.myourforum.api;

import java.util.List;

import myour.myourforum.model.Category;
import myour.myourforum.model.Post;
import myour.myourforum.model.User;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface RESTfulAPIRequest {

    @GET("/categories")
    Call<List<Category>> getAllCategory();

    @GET("/posts/by-category-id")
    Call<List<Post>> getPostByCategory(@Query("categoryId") int categoryId, @Query("pageIdx") int pageIdx, @Query("size") int size);

    @POST("/image")
    @Multipart
    Call<String> uploadImage(@Part MultipartBody.Part image);

    @GET("/users/username/by-user-id")
    Call<String> getUserNameByUserId(@Query("userId") int userId);

    @POST("/users/register")
    Call<String> registerUser(@Body User user);
}
