package mx.jbl.ejemplo1

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import mx.jbl.ejemplo1.data.Receta

import mx.jbl.ejemplo1.dummy.DummyContent
import mx.jbl.ejemplo1.viewModels.RecetaViewModel

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [RecetaMainFragment.OnListFragmentInteractionListener] interface.
 */
class RecetaMainFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    private lateinit var recetaVireModel : RecetaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recetaVireModel = activity?.run{

            ViewModelProviders.of(this).get(RecetaViewModel::class.java)
        }?: throw Exception("Invalid Activity!")

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_receta, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = RecetaItemRecyclerViewAdapter(
                    DummyContent.ITEMS,
                    object : OnListFragmentInteractionListener{
                        override fun onListFragmentInteraction(item: Receta?) {
                            Log.w("TAGGG","El item"+ item.toString())

                            item?.let{receta ->
                                recetaVireModel.setReceta(receta)
                            }

                            findNavController().navigate(R.id.action_recetaMainFragment_to_recetaDetalleFragment)
                        }
                    }
                )
            }
        }
        return view
    }



    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Receta?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            RecetaMainFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
