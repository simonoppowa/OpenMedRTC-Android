package software.openmedrtc.android.features.patient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import software.openmedrtc.android.R

class MedicalsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medicals_list, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MedicalsListFragment()
    }
}
