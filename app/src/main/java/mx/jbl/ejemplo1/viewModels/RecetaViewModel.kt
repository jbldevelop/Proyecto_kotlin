package mx.jbl.ejemplo1.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mx.jbl.ejemplo1.data.Receta

class RecetaViewModel :ViewModel(){

    val recetaSeleccionada : MutableLiveData<Receta> = MutableLiveData()


    fun setReceta(receta: Receta){
        recetaSeleccionada.value = receta

    }

}