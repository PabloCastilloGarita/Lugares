package com.lugares.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.lugares.model.Lugar
import java.util.ArrayList


class LugarDao {
    private val coleccion1 = "LugaresAPP"
    private val usuario = Firebase.auth.currentUser?.email.toString()
    private val coleccion2 = "misLugares"

    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    //Funcion para obtener la lista de lugares
    fun getLugares() : MutableLiveData<List<Lugar>> {
        val listaLugares = MutableLiveData<List<Lugar>>()

        firestore.collection(coleccion1).document(usuario).collection(coleccion2)
            .addSnapshotListener{ instantanea, e ->
                if(e != null){
                    return@addSnapshotListener
                }

                if(instantanea != null){
                    val lista = ArrayList<Lugar>()
                    instantanea.documents.forEach {
                        val lugar = it.toObject(Lugar::class.java)
                        if ( lugar != null){
                            lista.add(lugar)
                        }
                    }
                    listaLugares.value = lista
                }
            }


        return  listaLugares
    }

    suspend fun saveLugar(lugar: Lugar){
        val documento : DocumentReference
        if (lugar.id.isEmpty()){
            documento = firestore.collection(coleccion1).document(usuario).collection(coleccion2).document()
            lugar.id = documento.id
        }else {
            documento = firestore.collection(coleccion1).document(usuario).collection(coleccion2).document(lugar.id)
        }
        documento.set(lugar)
            .addOnSuccessListener { Log.d("saveLugar", "Lugar agregado/modificado") }
            .addOnSuccessListener { Log.d("saveLugar", "Error: Lugar NO agregado/modificado") }

    }

    suspend fun deleteLugar(lugar: Lugar){
        if (lugar.id.isNotEmpty()){
            firestore.collection(coleccion1).document(usuario).collection(coleccion2).document(lugar.id).delete()
                .addOnSuccessListener { Log.d("deleteLugar", "Lugar eliminado") }
                .addOnSuccessListener { Log.d("deleteLugar", "Error: Lugar NO eliminado") }

        }
    }
}