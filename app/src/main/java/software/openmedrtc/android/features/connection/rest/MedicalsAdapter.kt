package software.openmedrtc.android.features.connection.rest

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.list_item_medical.view.*
import software.openmedrtc.android.R
import software.openmedrtc.android.features.connection.entity.Medical
import kotlin.properties.Delegates


class MedicalsAdapter(private val context: Context) :
    RecyclerView.Adapter<MedicalsAdapter.MedicalsViewHolder>() {

    internal var collection: List<Medical> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    internal var clickListener: (Medical) -> Unit = { _ -> }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalsViewHolder =
        MedicalsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.list_item_medical,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = collection.size

    override fun onBindViewHolder(holder: MedicalsViewHolder, position: Int) {
        holder.bind(collection[position], clickListener)
    }


    class MedicalsViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(medical: Medical, clickListener: (Medical) -> Unit) {
            val context = view.context
            view.txtFullName.text = context.getString(
                R.string.name_template,
                medical.title,
                medical.firstName,
                medical.lastName
            ).trim()
            view.txtJobDescription.text = medical.description
            view.txtWaitingTime.text = context.getString(R.string.waiting_time_template, medical.waitingTime)

            // Load profile pic with Glide
            Glide
                .with(context)
                .load(medical.profilePicUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_account_circle_black_18dp)
                .circleCrop()
                .into(view.imgProfilePic)

            view.btnCallMedical.setOnClickListener { clickListener(medical) }
        }
    }
}
