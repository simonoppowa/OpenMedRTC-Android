package software.openmedrtc.android.features.connection.websocket

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_patient.view.*
import software.openmedrtc.android.R
import software.openmedrtc.android.features.connection.entity.Patient
import kotlin.properties.Delegates

class PatientAdapter(private val context: Context): RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    internal var collection: List<Patient> by Delegates.observable(emptyList()) {
            _, _, _ -> notifyDataSetChanged()
    }

    internal var clickListener: (Patient) -> Unit = { _-> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder =
        PatientViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_patient,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = collection.size

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        holder.bind(collection[position], clickListener)
    }


    class PatientViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(patient: Patient, clickListener: (Patient) -> Unit) {
            val context = view.context
            view.txtFullName.text = context.getString(
                R.string.name_template,
                patient.title,
                patient.firstName,
                patient.lastName
            ).trim()

            // Load profile pic with Glide
            Glide
                .with(context)
                .load(patient.profilePicUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_account_circle_black_18dp)
                .circleCrop()
                .into(view.imgProfilePic)

            view.btnCallPatient.setOnClickListener { clickListener(patient) }
        }
    }
}
