package software.openmedrtc.android

import android.os.Bundle
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.dashboard.DashboardActivity

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        checkUserCredentials()
        finish()
    }

    private fun checkUserCredentials() {
        // TODO user authentication
        startActivity(DashboardActivity.getIntent(this))
    }
}
