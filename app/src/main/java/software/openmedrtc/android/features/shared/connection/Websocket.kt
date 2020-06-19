package software.openmedrtc.android.features.shared.connection

import okhttp3.*
import okio.ByteString
import timber.log.Timber
import java.util.*

class Websocket(client: OkHttpClient, url: String, socketListener: SocketListener) {

    private val socketListeners = LinkedList<SocketListener>().apply {
        add(socketListener)
    }

    private var webSocket: WebSocket = client.newWebSocket(
        Request.Builder()
            .url(url)
            .build(),
        registerListener()
    )

    private fun registerListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Timber.d("onOpen")
                socketListeners.forEach { it.onOpen(this@Websocket, response) }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Timber.d("onMessage: $text")
                socketListeners.forEach { it.onMessage(this@Websocket, text) }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Timber.d("onFailure: $t")
                socketListeners.forEach { it.onFailure(this@Websocket, t, response) }
            }
        }
    }

    fun addListener(socketListener: SocketListener) {
        socketListeners.add(socketListener)
    }

    fun removeLister(socketListener: SocketListener) {
        socketListeners.remove(socketListener)
    }

    fun sendMessage(text: String) {
        webSocket.send(text)
    }

    interface SocketListener {
        fun onOpen(websocket: Websocket, response: Response)
        fun onFailure(websocket: Websocket, t: Throwable, response: Response?)
        fun onMessage(websocket: Websocket, text: String)
    }
}
