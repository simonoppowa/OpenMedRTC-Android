package software.openmedrtc.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import software.openmedrtc.android.core.PatientMainFragment
import software.openmedrtc.android.core.platform.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openFragment(PatientMainFragment(), R.id.main_fragment_container)
    }
}
