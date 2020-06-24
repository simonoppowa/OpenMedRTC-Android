package software.openmedrtc.android.features.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import software.openmedrtc.android.R
import software.openmedrtc.android.core.platform.BaseActivity

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    companion object {

        fun getIntent(context: Context) =
            Intent(context, LoginActivity::class.java)
    }
}
