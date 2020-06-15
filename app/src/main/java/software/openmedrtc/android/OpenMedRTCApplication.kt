package software.openmedrtc.android

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.logger.AndroidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class OpenMedRTCApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Init Koin
        startKoin {
            AndroidLogger()
            androidContext(this@OpenMedRTCApplication)
            modules(applicationModule)
        }
        // Init Timber
        Timber.plant(Timber.DebugTree())
    }
}
