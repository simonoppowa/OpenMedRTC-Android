package software.openmedrtc.android.features.dashboard

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.koin.android.ext.android.get
import software.openmedrtc.android.R
import software.openmedrtc.android.core.authentication.Authenticator
import software.openmedrtc.android.core.platform.BaseActivity
import software.openmedrtc.android.features.dashboard.medical.MedicalDashboardFragment
import software.openmedrtc.android.features.dashboard.patient.PatientDashboardFragment
import timber.log.Timber

class DashboardActivity : BaseActivity() {

    private val authenticator: Authenticator = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        checkMediaPermissions()
    }

    private fun initFragment() {
        when {
            authenticator.isMedical() -> {
                openFragment(
                    MedicalDashboardFragment.newInstance(),
                    R.id.main_fragment_container
                )
            }
            authenticator.isPatient() -> {
                openFragment(
                    PatientDashboardFragment.newInstance(),
                    R.id.main_fragment_container
                )
            }
            else -> {
                // TODO logged in user not valid
            }
        }
    }

    private fun checkMediaPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PERMISSION_GRANTED_CODE || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PERMISSION_GRANTED_CODE
        ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                ),
                ALL_PERMISSIONS_CODE
            )
        } else {
            initFragment()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == ALL_PERMISSIONS_CODE && grantResults.size == 2
            && grantResults[0] == PERMISSION_GRANTED_CODE
            && grantResults[1] == PERMISSION_GRANTED_CODE
        ) {
            // Permissions granted
            initFragment()
        } else {
            // TODO permissions not granted
            Timber.e("Permissions not granted")
            finish()
        }
    }

    companion object {
        private const val ALL_PERMISSIONS_CODE = 1
        private const val PERMISSION_GRANTED_CODE = PackageManager.PERMISSION_GRANTED

        fun getIntent(context: Context) =
            Intent(context, DashboardActivity::class.java)
    }
}
