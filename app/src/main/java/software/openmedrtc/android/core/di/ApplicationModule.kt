package software.openmedrtc.android.core.di

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.PeerConnectionFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import software.openmedrtc.android.BuildConfig
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.features.medical.MedicalViewModel
import software.openmedrtc.android.features.medical.PatientAdapter
import software.openmedrtc.android.features.patient.GetMedicals
import software.openmedrtc.android.features.patient.MedicalsAdapter
import software.openmedrtc.android.features.patient.PatientViewModel
import software.openmedrtc.android.features.shared.UserRepository
import software.openmedrtc.android.features.shared.UserService
import software.openmedrtc.android.features.shared.connection.*
import software.openmedrtc.android.features.shared.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.shared.connection.sdp.SessionDescriptionRepository
import software.openmedrtc.android.features.shared.connection.sdp.SetSessionDescription
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
            .addConverterFactory(GsonConverterFactory.create())
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
        GetMedicals(get(), get(), get())
    }

    factory {
        GetWebsocketConnection(get(), get(), get())
    }

    factory {
        GetPeerConnection(get(), get(), get())
    }

    factory {
        GetSessionDescription(get(), get(), get())
    }

    factory {
        SetSessionDescription(get(), get())
    }

    // ViewModels
    viewModel {
        PatientViewModel(get(), get())
    }

    viewModel {
        MedicalViewModel(get(), get())
    }

    viewModel {
        PatientConnectionViewModel(get(), get(), get())
    }

    viewModel {
        MedicalConnectionViewModel(get(), get(), get(), get(), get())
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
