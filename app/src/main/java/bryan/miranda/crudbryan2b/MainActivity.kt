package bryan.miranda.crudbryan2b

import RecyclerViewHelper.Adaptador
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modelo.ClaseConexion
import modelo.dataClassMascotas
import java.util.UUID

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //1- Mandar a llamar a todos los elementos
        val txtNombre = findViewById<EditText>(R.id.txtNombre)
        val txtPeso = findViewById<EditText>(R.id.txtPeso)
        val txtEdad = findViewById<EditText>(R.id.txtEdad)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val rcvMascotas = findViewById<RecyclerView>(R.id.rcvMascotas)

        //Primer paso para mostrar datos
        //Asignarle un layout al RecyclerView
        rcvMascotas.layoutManager = LinearLayoutManager(this)


        /////////////////// TODO: Mostrar datos ////////////////////
        //Funcion para mostrar los datos
        fun obtenerDatos(): List<dataClassMascotas>{
            //1- Creo un objeto de la clase conexion
            val objConexion = ClaseConexion().cadenaConexion()

            //2- Creo un Statement
            val statement = objConexion?.createStatement()
            val resulSet = statement?.executeQuery("select * from tbMascotas")!!


            val mascotas = mutableListOf<dataClassMascotas>()

            //Recorro todos los registros de la base de datos
            while (resulSet.next()){
                val uuid = resulSet.getString("uuid")
                val nombre = resulSet.getString("nombreMascota")
                val peso = resulSet.getInt("peso")
                val edad = resulSet.getInt("edad")

                val mascota = dataClassMascotas(uuid, nombre, peso, edad)
                mascotas.add(mascota)
            }
            return mascotas
        }
         
        //Ultimo paso
        //Asignar el adaptador al RecyclerView
        CoroutineScope(Dispatchers.IO).launch {
            val mascotasDB = obtenerDatos()
            withContext(Dispatchers.Main){
                val adapter = Adaptador(mascotasDB)
                rcvMascotas.adapter = adapter
            }
        }



        //2- Programar el boton para agregar
        btnAgregar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                //1- Creo un objeto de la clase conexion
                val objConexion = ClaseConexion().cadenaConexion()

                //2- Creo una variable que contenga un PrepareStatement
                val addMascota = objConexion?.prepareStatement("insert into tbMascotas(uuid, nombreMascota, peso, edad) values(?, ?, ?, ?)")!!
                addMascota.setString(1, UUID.randomUUID().toString())
                addMascota.setString(2, txtNombre.text.toString())
                addMascota.setInt(3,  txtPeso.text.toString().toInt())
                addMascota.setInt(4, txtEdad.text.toString().toInt())
                addMascota.executeUpdate()

                //Refresco la lista
                val nuevasMascotas = obtenerDatos()
                withContext(Dispatchers.Main){
                    (rcvMascotas.adapter as? Adaptador)?.actualizarLista(nuevasMascotas)
                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}