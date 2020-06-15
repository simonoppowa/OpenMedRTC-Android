package software.openmedrtc.android

import android.os.Bundle
import software.openmedrtc.android.core.platform.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
