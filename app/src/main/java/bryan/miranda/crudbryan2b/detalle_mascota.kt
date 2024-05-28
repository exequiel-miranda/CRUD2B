package bryan.miranda.crudbryan2b

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class detalle_mascota : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_mascota)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Recibir los valores
        val UUIDRecibido = intent.getStringExtra("MascotaUUID")
        val nombreRecibido = intent.getStringExtra("nombre")
        val pesoRecibido = intent.getIntExtra("peso", 0)
        val edadRecibida = intent.getIntExtra("edad",0)

        //Mando a llamar a todos los elementos de la pantalla
        val txtUUIDDetalle = findViewById<TextView>(R.id.txtUUIDDetalle)
        val txtNombreDetalle = findViewById<TextView>(R.id.txtNombreDetalle)
        val txtPesoDetalle = findViewById<TextView>(R.id.txtPesoDetalle)
        val txtEdadDetalle = findViewById<TextView>(R.id.txtEdadDetalle)

        //Asigarle los datos recibidos a mis TextView
        txtUUIDDetalle.text = UUIDRecibido
        txtNombreDetalle.text = nombreRecibido
        txtPesoDetalle.text = pesoRecibido.toString()
        txtEdadDetalle.text = edadRecibida.toString()
    }
}