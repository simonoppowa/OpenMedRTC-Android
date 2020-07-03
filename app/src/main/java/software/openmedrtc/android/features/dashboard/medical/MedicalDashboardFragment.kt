package software.openmedrtc.android.features.dashboard.medical

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_patient_dashboard.*
import software.openmedrtc.android.R
import software.openmedrtc.android.core.platform.BaseFragment
import software.openmedrtc.android.features.dashboard.patient.MedicalsListFragment

class MedicalDashboardFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medical_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFragmentView()
        setBottomNavigationViewListener()
    }

    private fun initFragmentView() {
        openFragment(
            PatientsListFragment.newInstance(),
            DEFAULT_FRAGMENT_ID
        )
    }

    private fun setBottomNavigationViewListener() {
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId) {
                R.id.navigation_patients -> {
                    openFragment(MedicalsListFragment.newInstance(), R.id.patients_list_container)
                    true
                }
                else -> false
            }
        }
    }
    companion object {
        private const val DEFAULT_FRAGMENT_ID = R.id.patients_list_container

        @JvmStatic
        fun newInstance() =
            MedicalDashboardFragment()
    }
}
