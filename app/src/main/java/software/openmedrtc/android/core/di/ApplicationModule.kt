package software.openmedrtc.android.core.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import okhttp3.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import software.openmedrtc.android.BuildConfig
import software.openmedrtc.android.features.patient.GetMedicals
import software.openmedrtc.android.features.patient.MedicalsAdapter
import software.openmedrtc.android.features.patient.PatientViewModel
import software.openmedrtc.android.features.shared.UserRepository
import software.openmedrtc.android.features.shared.UserService
import java.io.IOException

val applicationModule = module(override = true) {

    // Connection
    single {
        createClient()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
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

    factory {
        GetMedicals(get(), get(), get())
    }

    // ViewModels
    viewModel {
        PatientViewModel()
    }

    // Adapters
    factory {
        MedicalsAdapter(
            androidApplication()
        )
    }

    // UseCases
    single {
        GetMedicals(get(), get(), get())
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
                val credential = Credentials.basic("android_29@gmail.com", "test") // TODO
                return response.request.newBuilder()
                    .header("Authorization", credential)
                    .build()
            }
        }).build()
}
