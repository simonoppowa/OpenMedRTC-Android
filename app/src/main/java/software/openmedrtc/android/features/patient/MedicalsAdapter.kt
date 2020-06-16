package software.openmedrtc.android.features.patient

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_medical.view.*
import software.openmedrtc.android.R
import software.openmedrtc.android.features.shared.Medical
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
            view.txtOnlineStatus.text = view.context.getString(R.string.user_online)
            view.txtFullName.text = "${medical.firstName} ${medical.lastName}"
            view.txtJobDescription.text = "General Practitioner"

            view.btnCall.setOnClickListener { clickListener(medical) }
        }
    }
}
