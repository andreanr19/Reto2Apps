package edu.co.icesi.pokeapi.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import edu.co.icesi.pokeapi.R
import edu.co.icesi.pokeapi.databinding.ActivityMainBinding
import edu.co.icesi.pokeapi.model.User
import edu.co.icesi.pokeapi.util.HTTPSWebUtilDomi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        binding.loginBtn.setOnClickListener(::login)
    }

    private fun login(view: View){

        var username = binding.usernameET.text.toString().trim()

        if(username!=""){

            val user = User(UUID.randomUUID().toString(), username)

            //realizar la busqueda de si el usuario ya existe en la base de datos
            val query = Firebase.firestore.collection("users").whereEqualTo("username",username)

            query.get().addOnCompleteListener { task->
                //si el usuario no existe
                if(task.result?.size()==0){
                    Firebase.firestore.collection("users").document(user.id).set(user)
                    val intent = Intent(this, PokedexActivity::class.java).apply {
                        putExtra("user", user)
                    }
                    startActivity(intent)

                }else{
                //si el usuario ya existe
                    lateinit var existingUser : User
                    for(document in task.result!!){
                        existingUser = document.toObject(User::class.java)
                        break
                    }
                    val intent = Intent(this, PokedexActivity::class.java).apply {
                        putExtra("user", existingUser)
                    }
                    startActivity(intent)
                }
            }
        }else{
            Toast.makeText(this, "You have to enter the username!", Toast.LENGTH_LONG).show()
        }


    }
}