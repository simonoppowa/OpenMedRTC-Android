package software.openmedrtc.android

import android.os.Bundle
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.medical.MedicalMainFragment
import software.openmedrtc.android.features.patient.PatientMainFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TODO
        if(android.os.Build.VERSION.SDK_INT == 26) {
            openFragment(PatientMainFragment.newInstance(), R.id.main_fragment_container)
        } else {
            openFragment(MedicalMainFragment.newInstance(), R.id.main_fragment_container)
        }
    }
}
