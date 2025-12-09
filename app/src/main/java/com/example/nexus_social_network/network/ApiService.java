package com.example.nexus_social_network.network;

import com.example.nexus_social_network.responce.AvatarResponse;
import com.example.nexus_social_network.Models.ChangePasswordDTO;
import com.example.nexus_social_network.Models.ChatDTO;
import com.example.nexus_social_network.Models.ChatMessageDTO;
import com.example.nexus_social_network.Models.CommentCreatedResponse;
import com.example.nexus_social_network.Models.CommentDTO;
import com.example.nexus_social_network.Models.CreateChatDTO;
import com.example.nexus_social_network.Models.CreateCommentDTO;
import com.example.nexus_social_network.Models.EditProfileRequest;
import com.example.nexus_social_network.Models.LoginRequest;
import com.example.nexus_social_network.responce.LoginResponse;
import com.example.nexus_social_network.Models.MarkAsReadDTO;
import com.example.nexus_social_network.responce.MessageResponse;
import com.example.nexus_social_network.Models.MessageResponseWithChat;
import com.example.nexus_social_network.Models.PostDTO;
import com.example.nexus_social_network.Models.PostFeedItemDTO;
import com.example.nexus_social_network.responce.PostResponseDTO;
import com.example.nexus_social_network.Models.RegisterRequest;
import com.example.nexus_social_network.responce.RegisterResponse;
import com.example.nexus_social_network.Models.SendMessageDTO;
import com.example.nexus_social_network.Models.UpdateMessageDTO;
import com.example.nexus_social_network.Models.UserLikeDTO;
import com.example.nexus_social_network.Models.UserProfileDTO;
import com.example.nexus_social_network.responce.UserProfileResponse;
import com.example.nexus_social_network.responce.UserResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/register")
    Call<RegisterResponse> registerUser(@Body RegisterRequest request);

    @POST("/login")
    Call<LoginResponse> loginUser(@Body LoginRequest request);
    @GET("/me")
    Call<UserProfileResponse> getMyProfile(@Header("Authorization") String token);
    @GET("/me")
    Call<UserResponse> getMe(@Header("Authorization") String token);



    @PUT("/me")
    Call<UserResponse> updateProfile(
            @Header("Authorization") String token,
            @Body EditProfileRequest request
    );

    @Multipart
    @POST("/me/avatar")
    Call<AvatarResponse> uploadAvatar(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file
    );

    @DELETE("/me/avatar")
    Call<MessageResponse> deleteAvatar(
            @Header("Authorization") String token
    );

    @Multipart
    @POST("/posts")
    Call<PostResponseDTO> createPost(
            @Header("Authorization") String token,
            @Part("contentText") RequestBody contentText,
            @Part MultipartBody.Part image
    );

    @GET("/posts")
    Call<List<PostDTO>> getPosts(@Header("Authorization") String token);


    @GET("/posts/{id}")
    Call<PostDTO> getPostDetails(
            @Header("Authorization") String token,
            @Path("id") int postId
    );

    @GET("/posts/{id}/comments")
    Call<List<CommentDTO>> getComments(
            @Path("id") int postId
    );

    @POST("/posts/{id}/comments")
    Call<CommentCreatedResponse> addComment(
            @Header("Authorization") String token,
            @Path("id") int postId,
            @Body CreateCommentDTO dto
    );

    @POST("/posts/{id}/like")
    Call<Void> likePost(@Header("Authorization") String token, @Path("id") int postId);

    @DELETE("/posts/{id}/like")
    Call<Void> unlikePost(@Header("Authorization") String token, @Path("id") int postId);

    @GET("/posts/{id}/likes")
    Call<List<UserLikeDTO>> getPostLikes(@Path("id") int postId);

    @GET("users/{username}")
    Call<UserProfileDTO> getUserByUsername(
            @Path("username") String username,
            @Header("Authorization") String token
    );

    @GET("posts/search")
    Call<List<PostFeedItemDTO>> searchPosts(
            @Query("q") String query,
            @Header("Authorization") String token
    );
    // Чат методы
    @POST("/chats")
    Call<MessageResponseWithChat> createOrGetChat(
            @Header("Authorization") String token,
            @Body CreateChatDTO createChatDTO
    );

    @GET("/chats")
    Call<List<ChatDTO>> getUserChats(
            @Header("Authorization") String token,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("/chats/{chatId}")
    Call<ChatDTO> getChatInfo(
            @Header("Authorization") String token,
            @Path("chatId") int chatId
    );

    @DELETE("/chats/{chatId}")
    Call<MessageResponseWithChat> deleteChat(
            @Header("Authorization") String token,
            @Path("chatId") int chatId
    );

    // Сообщения
    @POST("/chats/{chatId}/messages")
    Call<MessageResponseWithChat> sendMessage(
            @Header("Authorization") String token,
            @Path("chatId") int chatId,
            @Body SendMessageDTO sendMessageDTO
    );

    @GET("/chats/{chatId}/messages")
    Call<List<ChatMessageDTO>> getChatMessages(
            @Header("Authorization") String token,
            @Path("chatId") int chatId,
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @PUT("/messages/{messageId}")
    Call<MessageResponseWithChat> updateMessage(
            @Header("Authorization") String token,
            @Path("messageId") int messageId,
            @Body UpdateMessageDTO updateMessageDTO
    );

    @DELETE("/messages/{messageId}")
    Call<MessageResponseWithChat> deleteMessage(
            @Header("Authorization") String token,
            @Path("messageId") int messageId
    );

    @POST("/messages/mark-as-read")
    Call<MessageResponseWithChat> markMessagesAsRead(
            @Header("Authorization") String token,
            @Body MarkAsReadDTO markAsReadDTO
    );

    @GET("/messages/search")
    Call<List<ChatMessageDTO>> searchMessages(
            @Header("Authorization") String token,
            @Query("q") String query
    );
    @PUT("/me/password")
    Call<Map<String, String>> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordDTO changePasswordDTO
    );
    @DELETE("/account")
    Call<Void> deleteAccount(
            @Header("Authorization") String token
    );

}
