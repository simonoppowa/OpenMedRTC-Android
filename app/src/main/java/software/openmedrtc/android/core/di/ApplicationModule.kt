package software.openmedrtc.android.core.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.webrtc.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import software.openmedrtc.android.BuildConfig
import software.openmedrtc.android.core.helper.FrontVideoCapturer
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.features.dashboard.medical.MedicalViewModel
import software.openmedrtc.android.features.connection.websocket.PatientAdapter
import software.openmedrtc.android.features.dashboard.patient.PatientViewModel
import software.openmedrtc.android.features.connection.*
import software.openmedrtc.android.features.connection.entity.UserDTO
import software.openmedrtc.android.features.connection.peerconnection.GetPeerConnection
import software.openmedrtc.android.features.connection.rest.*
import software.openmedrtc.android.features.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.connection.sdp.SessionDescriptionRepository
import software.openmedrtc.android.features.connection.sdp.SetSessionDescription
import software.openmedrtc.android.features.connection.websocket.GetWebsocketConnection
import software.openmedrtc.android.features.connection.websocket.WebsocketRepository
import java.io.IOException

// TODO remove mocked data
private val DEVICE_NAME = "android_" + android.os.Build.VERSION.SDK_INT + "@gmail.com"
val USERNAME = DEVICE_NAME
const val PASSWORD = "test"

private const val HTTP_PROTOCOL = "http://"
private const val PORT = BuildConfig.BASE_PORT


val applicationModule = module(override = true) {

    // Connection
    single {
        createClient()
    }

    single {
        Retrofit.Builder()
            .baseUrl("$HTTP_PROTOCOL${BuildConfig.BASE_URL}:$PORT")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(createGson()))
            .build()
    }

    single {
        Gson()
    }

    single {
        JsonParser(get())
    }

    single {
        EglBase.create()
    }

    single {
        createPeerConnectionFactory(androidApplication(), get())
    }

    // View
    single {
        MediaConstraints()
    }

    single {
        getSurfaceTextureHelper(get())
    }

    // Camera
    single {
        FrontVideoCapturer()
    }

    // Coroutines
    factory { CoroutineScope(Dispatchers.IO + Job()) }
    factory { Dispatchers.Main as CoroutineDispatcher }

    // Services
    single {
        UserService(get())
    }

    // Repositories
    single {
        UserRepository.Network(get())
    }

    single {
        WebsocketRepository.WebsocketRepositoryImpl(get()) as WebsocketRepository
    }

    single {
        SessionDescriptionRepository.SessionDescriptionRepositoryImpl()
                as SessionDescriptionRepository
    }

    // UseCases
    factory {
        GetMedicals(
            get(),
            get(),
            get()
        )
    }

    factory {
        AuthenticateUser(get(), get(), get())
    }

    factory {
        GetWebsocketConnection(
            get(),
            get(),
            get()
        )
    }

    factory {
        GetPeerConnection(
            get(),
            get(),
            get()
        )
    }

    factory {
        GetSessionDescription(get(), get(), get())
    }

    factory {
        SetSessionDescription(get(), get())
    }

    // ViewModels
    viewModel {
        PatientViewModel(
            get(),
            get()
        )
    }

    viewModel {
        MedicalViewModel(
            get(),
            get()
        )
    }

    viewModel {
        PatientConnectionViewModel(get(), get(), get(), get(), get(), get(), get())
    }

    viewModel {
        MedicalConnectionViewModel(get(), get(), get(), get(), get(), get(), get())
    }

    // Adapters
    factory {
        MedicalsAdapter(
            androidApplication()
        )
    }

    factory {
        PatientAdapter(
            androidApplication()
        )
    }

}

private fun createClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .authenticator(object : Authenticator {
            @Throws(IOException::class)
            override fun authenticate(route: Route?, response: Response): Request? {
                if (response.request.header("Authorization") != null) {
                    return null // Give up, we've already attempted to authenticate.
                }

                println("Authenticating for response: $response")
                println("Challenges: ${response.challenges()}")
                val credential = Credentials.basic(USERNAME, PASSWORD)
                return response.request.newBuilder()
                    .header("Authorization", credential)
                    .build()
            }
        }).build()
}

private fun createGson() =
    GsonBuilder().registerTypeAdapter(UserDTO::class.java, UserDeserializer()).create()

private fun createPeerConnectionFactory(
    context: Context,
    rootEglBase: EglBase
): PeerConnectionFactory {
    val initializationOptions =
        PeerConnectionFactory.InitializationOptions.builder(context)
            .createInitializationOptions()
    PeerConnectionFactory.initialize(initializationOptions)
    val options = PeerConnectionFactory.Options()

    return PeerConnectionFactory.builder()
        .setOptions(options)
        .setVideoEncoderFactory(getVideoEncoderFactory(rootEglBase))
        .setVideoDecoderFactory(getVideoDecoderFactory(rootEglBase))
        .createPeerConnectionFactory()
}

private fun getVideoEncoderFactory(rootEglBase: EglBase) = DefaultVideoEncoderFactory(
    rootEglBase.eglBaseContext,
    true,
    true
)

private fun getVideoDecoderFactory(rootEglBase: EglBase) =
    DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)

private fun getSurfaceTextureHelper(eglBase: EglBase) =
    SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
