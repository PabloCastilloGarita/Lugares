package com.lugares.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.lugares.databinding.LugarFilaBinding
import com.lugares.model.Lugar
import com.lugares.ui.lugar.LugarFragmentDirections

class LugarAdapter : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {
    //una lista de los lugares a mostrar
    private var listaLugares = emptyList<Lugar>()

    inner class LugarViewHolder(private val itemBinding: LugarFilaBinding)
        : RecyclerView.ViewHolder(itemBinding.root) {
            fun bind(lugar : Lugar) {
                itemBinding.tvNombre.text = lugar.nombre
                itemBinding.tvCorreo.text = lugar.correo
                itemBinding.tvTelefono.text = lugar.telefono
                itemBinding.tvWeb.text = lugar.web

                itemBinding.vistaFila.setOnClickListener {
                    val accion = LugarFragmentDirections
                        .actionNavLugarToNavUpdateLugar(lugar)
                    itemView.findNavController().navigate(accion)
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val itemBinding = LugarFilaBinding.inflate(LayoutInflater.from(parent.context),
        parent,false)
        return LugarViewHolder(itemBinding) //devuelvo un card para 'pintar' un lugar
    }

    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        val lugar = listaLugares[position] //recupero el luegar a 'pintar'
        holder.bind(lugar)
    }

    override fun getItemCount(): Int {
        return listaLugares.size //para conocer la cantidad de "card" que debe hacer...
    }

    fun setData(lugares : List<Lugar>) {
        this.listaLugares = lugares
        notifyDataSetChanged()
    }
}