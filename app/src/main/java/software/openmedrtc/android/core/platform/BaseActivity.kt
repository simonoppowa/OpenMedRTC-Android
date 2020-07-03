package software.openmedrtc.android.core.platform

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import software.openmedrtc.android.core.functional.Failure

open class BaseActivity: AppCompatActivity() {

    // TODO duplicate code
    open fun openFragment(fragment: Fragment, containerId: Int) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(containerId, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    open fun finishWithFailure(failure: Failure) {
        Toast.makeText(this, failure.toString(), Toast.LENGTH_LONG).show()
        finish()
    }
}
