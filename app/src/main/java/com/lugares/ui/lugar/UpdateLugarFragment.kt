package com.lugares.ui.lugar

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lugares.R
import com.lugares.databinding.FragmentUpdateLugarBinding
import com.lugares.model.Lugar
import com.lugares.viewmodel.LugarViewModel

class UpdateLugarFragment : Fragment() {
    private lateinit var lugarViewModel: LugarViewModel
    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<UpdateLugarFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel = ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)

        //Obtengo al info del lugar y la coloco en el fragmento
        binding.etNombre.setText(args.lugar.nombre)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etWeb.setText(args.lugar.web)
        binding.tvLatitud.text=args.lugar.latitud.toString()
        binding.tvLongitud.text=args.lugar.longitud.toString()
        binding.tvAltura.text=args.lugar.altura.toString()

         binding.btUpdateLugar.setOnClickListener() { actualizarLugar() }
         binding.btEmail.setOnClickListener() { enviarCorreo() }
         binding.btPhone.setOnClickListener() { hacerLlamada() }
         binding.btWhatsapp.setOnClickListener() { enviarWhatsApp() }
         binding.btLocation.setOnClickListener() { verMapa() }
         binding.btWeb.setOnClickListener() { verWeb() }

        setHasOptionsMenu(true)//este fragmento debe tener un menu adicional

        return binding.root
    }

    private fun verMapa() {
        val latitud = binding.tvLatitud.text.toString().toDouble() //se extrae la latitud y se pasa a double
        val longitud = binding.tvLongitud.text.toString().toDouble() //se extrae la longitud y se pasa a double

        if (latitud.isFinite() && longitud.isFinite()) { //podemos usar el recurso...
            val location = Uri.parse("geo:$latitud,$longitud?z=18")
            val intent = Intent(Intent.ACTION_VIEW,location) //se ve le mapa desde el app

            startActivity(intent) // se abre el visor de mapas desde el lugar

        } else { //no podemos usar el recurso...
            Toast.makeText(requireContext(),getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }

    private fun verWeb() {
        val sitio = binding.etWeb.text.toString() //se extrae el sitio web del lugar
        if (sitio.isNotEmpty()) { //podemos usar el recurso...
            val web = Uri.parse("http://$sitio")
            val intent = Intent(Intent.ACTION_VIEW,web) //se va al sitio web desde el app

            startActivity(intent) // se hace el visor web y se muestra el sitio web

        } else { //no podemos usar el recurso...
            Toast.makeText(requireContext(),getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }

    private fun enviarWhatsApp() {
        val telefono = binding.etTelefono.text.toString() //se extrae el numero de telefono del lugar
        if (telefono.isNotEmpty()) { //podemos usar el recurso...
            val intent = Intent(Intent.ACTION_VIEW) //se va a enviar algo desde el app
            val uri = "whatsapp://send?phone=506$telefono&text="+ //con ese prefijo se sabe que es un numero de telefono
                getString(R.string.msg_saludos)

            //se establece el app a usar
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(uri) //se carga la info
            startActivity(intent)

        } else { //no podemos usar el recurso...
            Toast.makeText(requireContext(),getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }

    private fun hacerLlamada() {
        val telefono = binding.etTelefono.text.toString() //se extrae el numero de telefono del lugar
        if (telefono.isNotEmpty()) { //podemos usar el recurso...
            val intent = Intent(Intent.ACTION_CALL) //se va a enviar algo desde el app
            intent.data = Uri.parse("tel:$telefono") //con ese prefijo se sabe que es un numero de telefono

            //se procede a validar si hay permisos para hacer la llamada

            if (requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) !=
                    PackageManager.PERMISSION_GRANTED) { //si no tenemos los permisos hay que solicitarlos

                requireActivity().requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),105)
            } else { // si se han otorgado los permisos
                requireActivity().startActivity(intent) //se hace la llamada telefonica
            }
        } else { //no podemos usar el recurso...
            Toast.makeText(requireContext(),getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }

    private fun enviarCorreo() {
        val correo = binding.etCorreo.text.toString() //se extrae la cuenta de correo del lugar
        if (correo.isNotEmpty()) { //podemos usar el recurso...
            val intent = Intent(Intent.ACTION_SEND) //se va a enviar algo desde el app
            intent.type = "message/rfc822" //se va a enviar un correo electronico

            //se define el destinatario
            intent.putExtra(Intent.EXTRA_EMAIL,arrayOf(correo))

            //se define el asunto
            intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.msg_saludos)+" "+binding.etNombre.text)

            //se define el cuerpo del correo inicial
            intent.putExtra(Intent.EXTRA_TEXT,getString(R.string.msg_mensaje_correo))

            //se solicita el recurso de correo para que se envie este
            startActivity(intent)
        } else { //no podemos usar el recurso...
            Toast.makeText(requireContext(),getString(R.string.msg_datos),Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //si es delete ...
        if(item.itemId==R.id.delete_menu) {
            deleteLugar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun actualizarLugar() {
        val nombre = binding.etNombre.text.toString()
        if (nombre.isNotEmpty()) {
            val correo = binding.etCorreo.text.toString()
            val telefono = binding.etTelefono.text.toString()
            val web = binding.etWeb.text.toString()
            val lugar = Lugar(args.lugar.id, nombre, correo, telefono, web, args.lugar.latitud, args.lugar.longitud, args.lugar.altura, "", "")
            lugarViewModel.updateLugar(lugar)
            Toast.makeText(requireContext(),
            getString(R.string.msg_lugar_update),
            Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_nav_update_lugar_to_nav_lugar)
        }
    }

    private fun deleteLugar() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.menu_delete)
        builder.setMessage(getString(R.string.msg_seguroBorrar) + "${args.lugar.nombre}?")
        builder.setNegativeButton(getString(R.string.no)) {_,_ ->}
        builder.setPositiveButton(getString((R.string.si))) { _, _ ->
            lugarViewModel.deleteLugar(args.lugar)
            findNavController().navigate(R.id.action_nav_update_lugar_to_nav_lugar)
        }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}