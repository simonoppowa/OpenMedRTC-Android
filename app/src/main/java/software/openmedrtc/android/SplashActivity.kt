package software.openmedrtc.android

import android.os.Bundle
import androidx.lifecycle.Observer
import org.koin.android.ext.android.get
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.dashboard.DashboardActivity
import software.openmedrtc.android.features.login.LoginActivity

class SplashActivity : BaseActivity() {

    private val splashActivityViewModel: SplashActivityViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (splashActivityViewModel.checkUserCredentialsSaved()) {
            observerUserAuth()
            splashActivityViewModel.authenticateUser()
        } else {
            startActivity(LoginActivity.getIntent(this))
            finish()
        }
    }

    private fun observerUserAuth() {
        splashActivityViewModel.userAuthenticated.observe(this, Observer {
            if (it) {
                startActivity(DashboardActivity.getIntent(this))
                finish()
            }
        })
    }

}
