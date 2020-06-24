package software.openmedrtc.android.features.dashboard.patient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_patient_dashboard.*
import org.koin.android.ext.android.get
import software.openmedrtc.android.R
import software.openmedrtc.android.core.platform.BaseFragment

class PatientDashboardFragment : BaseFragment() {

    private var patientViewModel: PatientViewModel = get()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragmentView()
        setBottomNavigationViewListener()
    }

    private fun initFragmentView() {
        openFragment(
            MedicalsListFragment.newInstance(),
            DEFAULT_FRAGMENT_ID
        )
    }

    private fun setBottomNavigationViewListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.navigation_medicals -> {
                    openFragment(MedicalsListFragment.newInstance(), R.id.medicals_list_container)
                    true
                }
                // TODO Add menu items
                else -> false
            }
        }
    }

    companion object {
        private const val DEFAULT_FRAGMENT_ID = R.id.medicals_list_container

        @JvmStatic
        fun newInstance() =
            PatientDashboardFragment()
    }
}
