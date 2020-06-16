package software.openmedrtc.android.core.platform

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import java.lang.NullPointerException

open class BaseFragment : Fragment() {

    open fun openFragment(fragment: Fragment, containerId: Int) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            ?: throw NullPointerException("Cannot open fragment, activity null")

        transaction.replace(containerId, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
