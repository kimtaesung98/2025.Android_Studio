package com.example.deliveryapp2.data.network

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.*
import org.json.JSONObject

object WebSocketManager {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    // 이벤트를 UI로 전달할 Flow
    private val _eventFlow = MutableSharedFlow<String>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun connect() {
        // 에뮬레이터 주소 (ws://)
        val request = Request.Builder().url("ws://10.0.2.2:8080/ws").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WS", "Connected to Server")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WS", "Message Received: $text")
                // 예: {"type":"NEW_ORDER", ...}
                try {
                    val json = JSONObject(text)
                    val type = json.optString("type")
                    _eventFlow.tryEmit(type) // "NEW_ORDER" or "STATUS_UPDATE" 발송
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WS", "Connection Failed: ${t.message}")
            }
        })
    }
}