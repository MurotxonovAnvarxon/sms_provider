package uz.murotxonov.sms_provider.repo

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener


class WebSocketRepository {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _messages = MutableSharedFlow<Message>(replay = 0)
    val messages: SharedFlow<Message> = _messages.asSharedFlow()

    val jsonWSS = """{"type": "sms","to": "+998 91 671 48 35","content": "Your verification code is: 424242"}"""


    fun connect(url: String) {
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
//                Log.d("WSS", "onOpen: ${response.message}")

                //ToDO
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val jsonConfig = Json {
                    ignoreUnknownKeys = true // Prevents crashes if the API adds new fields
                }
                scope.launch {
                    val messageWS = jsonConfig.decodeFromString<Message>(jsonWSS)
                    Log.d("WSS", "message: ${messageWS}")
                    _messages.emit(messageWS)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                scope.launch {
                    _messages.emit(Message(type = "Error",to="", content = t.toString()))
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
            }
        })
    }

    fun sendMessage(text: String) {
        webSocket?.send(text)
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnect")
        webSocket = null
    }
}

@Serializable
data class Message(val type: String, val to: String, val content: String)
