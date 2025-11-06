package com.example.appname.shorts.data.remote.api

import com.example.appname.shorts.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ShortsApi {
    @GET("shorts")
    suspend fun getShorts(): Response<List<ShortsItemDto>>

    @POST("shorts/{id}/like")
    suspend fun likeShort(@Path("id") shortsId: Int): Response<Unit>

    @GET("shorts/{id}/comments")
    suspend fun getComments(@Path("id") shortsId: Int): Response<List<ShortsCommentDto>>

    @POST("shorts/{id}/comments")
    suspend fun submitComment(
        @Path("id") shortsId: Int,
        @Body comment: CommentRequestDto
    ): Response<ShortsCommentDto> // (Go 서버가 생성된 댓글을 반환)
}