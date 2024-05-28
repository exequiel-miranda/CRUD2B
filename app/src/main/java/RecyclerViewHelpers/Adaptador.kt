package RecyclerViewHelper

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import bryan.miranda.crudbryan2b.R
import bryan.miranda.crudbryan2b.detalle_mascota
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassMascotas
class Adaptador(private var Datos: List<dataClassMascotas>) : RecyclerView.Adapter<ViewHolder>() {
    fun actualizarLista(nuevaLista: List<dataClassMascotas>) {
        Datos = nuevaLista
        notifyDataSetChanged() // Notificar al adaptador sobre los cambios
    }

    fun actualicePantalla(uuid: String, nuevoNombre: String){
        val index = Datos.indexOfFirst { it.uuid == uuid }
        Datos[index].nombreMascota = nuevoNombre
        notifyDataSetChanged()
    }  



    /////////////////// TODO: Eliminar datos
    fun eliminarDatos(nombreMascota: String, posicion: Int){
        //Actualizo la lista de datos y notifico al adaptador
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        GlobalScope.launch(Dispatchers.IO){
            //1- Creamos un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Crear una variable que contenga un PrepareStatement
            val deleteMascota = objConexion?.prepareStatement("delete from tbMascotas where nombreMascota = ?")!!
            deleteMascota.setString(1, nombreMascota)
            deleteMascota.executeUpdate()

            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }
        Datos = listaDatos.toList()
        // Notificar al adaptador sobre los cambios
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }

    //////////////////////TODO: Editar datos
    fun actualizarDato(nuevoNombre: String, uuid: String){
        GlobalScope.launch(Dispatchers.IO){

            //1- Creo un objeto de la clase de conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- creo una variable que contenga un PrepareStatement
            val updateMascota = objConexion?.prepareStatement("update tbMascotas set nombreMascota = ? where uuid = ?")!!
            updateMascota.setString(1, nuevoNombre)
            updateMascota.setString(2, uuid)
            updateMascota.executeUpdate()

            withContext(Dispatchers.Main){
                actualicePantalla(uuid, nuevoNombre)
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)

        return ViewHolder(vista)
    }


    override fun getItemCount() = Datos.size




    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mascota = Datos[position]
        holder.textView.text = mascota.nombreMascota

        //todo: clic al icono de eliminar
        holder.imgBorrar.setOnClickListener {

            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Desea eliminar la mascota?")

            //Botones
            builder.setPositiveButton("Si") { dialog, which ->
                eliminarDatos(mascota.nombreMascota, position)
            }

            builder.setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }

        //Todo: icono de editar
        holder.imgEditar.setOnClickListener{
            //Creamos un Alert Dialog
            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar")
            builder.setMessage("¿Desea actualizar la mascota?")

            //Agregarle un cuadro de texto para
            //que el usuario escriba el nuevo nombre
            val cuadroTexto = EditText(context)
            cuadroTexto.setHint(mascota.nombreMascota)
            builder.setView(cuadroTexto)

            //Botones
            builder.setPositiveButton("Actualizar") { dialog, which ->
                actualizarDato(cuadroTexto.text.toString(), mascota.uuid)
            }

            builder.setNegativeButton("Cancelar"){dialog, which ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        //Todo: Clic a la card completa
        //Vamos a ir a otra pantalla donde me mostrará todos los datos
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            //Cambiar de pantalla a la pantalla de detalle
            val pantallaDetalle = Intent(context, detalle_mascota::class.java)
          //enviar a la otra pantalla todos mis valores
            pantallaDetalle.putExtra("MascotaUUID", mascota.uuid)
            pantallaDetalle.putExtra("nombre", mascota.nombreMascota)
            pantallaDetalle.putExtra("peso", mascota.peso)
            pantallaDetalle.putExtra("edad", mascota.edad)
            context.startActivity(pantallaDetalle)
        }




    }

}
