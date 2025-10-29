package com.example.appname.feed.data.remote.api

import com.example.appname.feed.data.remote.model.PostDto
import retrofit2.Response // 🚨 (1) [New]
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * [설계 의도 요약]
 * Retrofit이 사용할 'Feed' 관련 API 엔드포인트 명세입니다.
 * (Go 서버가 이 엔드포인트들을 구현해야 합니다.)
 */
interface FeedApi {

    /**
     * (Go 서버의 GET /posts)
     * 피드 게시물 목록을 가져옵니다.
     */
    @GET("posts") // (2) 🚨 베이스 URL 뒤에 붙는 경로
    suspend fun getPosts(): Response<List<PostDto>> // (3) 🚨 서버 응답을 PostDto 리스트로 받음

    /**
     * (Go 서버의 POST /posts/{id}/like)
     * 게시물에 '좋아요'를 누릅니다.
     */
    @POST("posts/{id}/like")
    suspend fun likePost(@Path("id") postId: Int): Response<Unit> // (4) 🚨 응답 바디가 없을 경우 Unit

    // TODO: implement details (댓글 제출, 댓글 가져오기 API 뼈대 추가)
}