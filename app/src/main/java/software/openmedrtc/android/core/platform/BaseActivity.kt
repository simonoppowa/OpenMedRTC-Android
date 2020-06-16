package software.openmedrtc.android.core.platform

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

open class BaseActivity: AppCompatActivity() {

    open fun openFragment(fragment: Fragment, containerId: Int) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(containerId, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
