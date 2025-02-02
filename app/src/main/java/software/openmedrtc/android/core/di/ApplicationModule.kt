package software.openmedrtc.android.core.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.webrtc.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import software.openmedrtc.android.BuildConfig
import software.openmedrtc.android.SplashActivityViewModel
import software.openmedrtc.android.core.authentication.Authenticator
import software.openmedrtc.android.core.helper.FrontVideoCapturer
import software.openmedrtc.android.core.helper.JsonParser
import software.openmedrtc.android.features.connection.MedicalConnectionViewModel
import software.openmedrtc.android.features.connection.PatientConnectionViewModel
import software.openmedrtc.android.features.connection.entity.UserDTO
import software.openmedrtc.android.features.connection.peerconnection.GetPeerConnection
import software.openmedrtc.android.features.connection.rest.*
import software.openmedrtc.android.features.connection.sdp.GetSessionDescription
import software.openmedrtc.android.features.connection.sdp.SessionDescriptionRepository
import software.openmedrtc.android.features.connection.sdp.SetSessionDescription
import software.openmedrtc.android.features.connection.websocket.GetWebsocketConnection
import software.openmedrtc.android.features.connection.websocket.PatientAdapter
import software.openmedrtc.android.features.connection.websocket.WebsocketRepository
import software.openmedrtc.android.features.dashboard.medical.MedicalViewModel
import software.openmedrtc.android.features.dashboard.patient.PatientViewModel
import software.openmedrtc.android.features.login.LoginViewModel

private const val HTTP_PROTOCOL = "http://"
private const val PORT = BuildConfig.BASE_PORT

const val ID_SPE_KEY = "Id"
const val PASSWORD_SPE_KEY = "Password"

private const val FILE_NAME_ESP = "shared_prefs"

val applicationModule = module(override = true) {

    // Encryption
    single {
        MasterKey.Builder(androidApplication(), MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }

    single {
        EncryptedSharedPreferences.create(
            androidApplication(),
            FILE_NAME_ESP,
            get(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Authentication
    single {
        Authenticator(get())
    }

    // Connection
    single {
        Retrofit.Builder()
            .baseUrl("$HTTP_PROTOCOL${BuildConfig.BASE_URL}:$PORT")
            .addConverterFactory(GsonConverterFactory.create(createGson()))
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
        UserService(get(), get())
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
        AuthenticateUser(get(), get(), get(), get())
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
        SplashActivityViewModel(get(), get())
    }

    viewModel {
        LoginViewModel(get(), get())
    }

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
        PatientConnectionViewModel(get(), get(), get(), get(), get(), get(), get(), get())
    }

    viewModel {
        MedicalConnectionViewModel(get(), get(), get(), get(), get(), get(), get(), get())
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

private fun getVideoEncoderFactory(rootEglBase: EglBase) =
    DefaultVideoEncoderFactory(
        rootEglBase.eglBaseContext,
        true,
        true
    )

private fun getVideoDecoderFactory(rootEglBase: EglBase) =
    DefaultVideoDecoderFactory(rootEglBase.eglBaseContext)

private fun getSurfaceTextureHelper(eglBase: EglBase) =
    SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)
