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

    val jsonWSS = """{"type": "sms","to": "+998 90 732 27 02","content": "Sizning verivikatsiya kodingiz:\"123456\"","id":"-100"}"""


    fun connect(url: String) {
        val request = Request.Builder().url(url).build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WSS", "onOpen: ${response.code}")
                //ToDO
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WSS", "onMessage: ${text}")

                val jsonConfig = Json {
                    ignoreUnknownKeys = true
                }

                scope.launch {
                    val messageWS = jsonConfig.decodeFromString<Message>(jsonWSS)
                    Log.d("WSS", "messageId: ${messageWS.id}")
                    _messages.emit(messageWS)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.d("WSS", "Failure: ${t.toString()}")

                scope.launch {
                    _messages.emit(Message(type = "Error",to="", content = t.toString(),id="-1"))
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WSS", "onClosing")
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
data class Message(val type: String, val to: String, val content: String,val id:String)
