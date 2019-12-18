package mx.jbl.ejemplo1


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import mx.jbl.ejemplo1.data.Receta
import mx.jbl.ejemplo1.viewModels.RecetaViewModel

/**
 * A simple [Fragment] subclass.
 */
class RecetaDetalleFragment : Fragment() {

    private lateinit var recetaVireModel : RecetaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recetaVireModel = activity?.run{

            ViewModelProviders.of(this).get(RecetaViewModel::class.java)
        }?: throw Exception("Invalid Activity!")


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val mainView = inflater.inflate(R.layout.fragment_receta_detalle,container,false)

        // Inflate the layout for this fragment
        recetaVireModel.recetaSeleccionada.observe(this, Observer<Receta>{receta->
            Log.w("TAGGG2222","La receta seleccionada es "+ receta.nombre)

            val imagen = mainView.findViewById<ImageView>(R.id.receta_detalle_imagen)

            context?.let { context ->
                Glide
                    .with(context)
                    .load(receta.imagen_link)
                    .centerCrop()
                    .into(imagen)
            }

            mainView.findViewById<TextView>(R.id.receta_detalle_titulo)
                .text = receta.nombre
            mainView.findViewById<TextView>(R.id.receta_detalle_descripcion)
                .text = receta.descripcion
            mainView.findViewById<TextView>(R.id.receta_detalle_completa)
                .text = receta.recetaDetalle

        })

        return mainView
    }


}
