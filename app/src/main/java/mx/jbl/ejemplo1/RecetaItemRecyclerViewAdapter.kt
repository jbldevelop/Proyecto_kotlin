package mx.jbl.ejemplo1

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide


import mx.jbl.ejemplo1.RecetaMainFragment.OnListFragmentInteractionListener
import mx.jbl.ejemplo1.dummy.DummyContent.ITEMS

import kotlinx.android.synthetic.main.fragment_recetaitem.view.*
import mx.jbl.ejemplo1.data.Receta

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class RecetaItemRecyclerViewAdapter(
    private val mValues: List<Receta>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<RecetaItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var parent : ViewGroup? = null
    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Receta
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_recetaitem, parent, false)
        this.parent = parent
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: Receta = mValues[position]
        holder.mIdView.text = item.nombre
        holder.mContentView.text = item.descripcion


        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }

        this.parent?.let {parent->
            Glide
                .with(parent)
                .load(item.imagen_link)
                .centerCrop()
                .into(holder.mImage)
        }

    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.nombre
        val mContentView: TextView = mView.descripcion
        val mImage : ImageView = mView.receta_detimagen

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
