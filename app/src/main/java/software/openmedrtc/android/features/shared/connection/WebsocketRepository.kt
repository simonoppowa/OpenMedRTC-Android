package software.openmedrtc.android.features.shared.connection

import okhttp3.OkHttpClient
import okhttp3.Response
import software.openmedrtc.android.BuildConfig
import software.openmedrtc.android.core.functional.Either
import software.openmedrtc.android.core.functional.Failure
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface WebsocketRepository {
    suspend fun websocket(medKey: String = ""): Either<Failure, Websocket>


    class WebsocketRepositoryImpl(
        private val client: OkHttpClient
    ) : WebsocketRepository {

        private var websocket: Websocket? = null

        companion object {
            private const val WEBSOCKET_PROTOCOL = "ws://"
            private const val MY_IP = BuildConfig.BASE_URL
            private const val MY_PORT = BuildConfig.BASE_PORT
            private const val PATH_CONNECT = "/connect"

            private const val WEBSOCKET_FULL_ADDRESS =
                "$WEBSOCKET_PROTOCOL$MY_IP:$MY_PORT$PATH_CONNECT"
        }

        override suspend fun websocket(medKey: String): Either<Failure, Websocket> {
            return if(websocket != null) {
                Either.Right(websocket as Websocket)
            } else {
                try {
                    createListener(medKey)

                    Either.Right(websocket as Websocket)

                } catch (t : Throwable) {
                    Either.Left(Failure.WebsocketFailure)
                }
            }
        }

        private suspend fun createListener(medKey: String = "") {
            // Suspend coroutine until listener called
            suspendCoroutine<Websocket> { cont ->

                val socketListener = object : Websocket.SocketListener {
                    override fun onOpen(websocket: Websocket, response: Response) {
                        cont.resume(websocket)
                    }

                    override fun onFailure(
                        websocket: Websocket,
                        t: Throwable,
                        response: Response?
                    ) {
                        cont.resume(websocket)
                    }

                    override fun onMessage(websocket: Websocket, text: String) {}
                }

                websocket =
                    Websocket(client, "$WEBSOCKET_FULL_ADDRESS/$medKey", socketListener)
            }
        }
    }

}
