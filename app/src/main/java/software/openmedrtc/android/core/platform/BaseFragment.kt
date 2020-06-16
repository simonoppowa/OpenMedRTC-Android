package software.openmedrtc.android.core.platform

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import software.openmedrtc.android.core.functional.Failure
import java.lang.NullPointerException

open class BaseFragment : Fragment() {

    open fun openFragment(fragment: Fragment, containerId: Int) {
        val transaction: FragmentTransaction = activity?.supportFragmentManager?.beginTransaction()
            ?: throw NullPointerException("Cannot open fragment, activity null")

        transaction.replace(containerId, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    open fun sendErrorToast(failure: Failure) {
        Toast.makeText(context, failure.toString(), Toast.LENGTH_LONG).show()
    }
}
