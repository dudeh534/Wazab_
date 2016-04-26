package com.ourincheon.wazap.facebook;
        import com.google.gson.internal.LinkedTreeMap;

        import java.util.List;

        import retrofit2.Call;
        import retrofit2.http.Field;
        import retrofit2.http.FormUrlEncoded;
        import retrofit2.http.GET;
        import retrofit2.http.Header;
        import retrofit2.http.Multipart;
        import retrofit2.http.POST;
        import retrofit2.http.Query;

/**
 * Created by YS on 2016-02-19.
 */
public interface HttpService {
    @FormUrlEncoded
    @POST("/facebook_oauth/users")
    Call<LinkedTreeMap> setUserInfo(
            @Header("access-token") String access_token,
            @Field("users_id") String users_id,
            @Field("username") String username,
            @Field("profile_image") String profile_img);

}