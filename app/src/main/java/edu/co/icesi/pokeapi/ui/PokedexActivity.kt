package edu.co.icesi.pokeapi.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.co.icesi.pokeapi.databinding.ActivityPokedexBinding
import edu.co.icesi.pokeapi.model.Pokemon
import edu.co.icesi.pokeapi.model.User
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.JsonObject
import edu.co.icesi.pokeapi.util.Constants
import edu.co.icesi.pokeapi.util.HTTPSWebUtilDomi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.FieldPosition


class PokedexActivity : AppCompatActivity() {

    private lateinit var binding : ActivityPokedexBinding
    private lateinit var pokemonRecycler : RecyclerView
    private lateinit var layoutManager: GridLayoutManager
    //private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter : PokemonsAdapter

    private lateinit var user : User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokedexBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //obtener el user loggeado
        user = intent.extras?.get("user") as User

        //elementos del recycler
        pokemonRecycler = binding.pokemonsRecyclerView
        layoutManager = GridLayoutManager(this, 2)
        pokemonRecycler.layoutManager =  layoutManager
        pokemonRecycler.setHasFixedSize(true)
        adapter = PokemonsAdapter()
        pokemonRecycler.adapter = adapter

        adapter.setOnPokemonItemClickListener(object : PokemonsAdapter.OnPokemonItemClickListenerInterface{
            override fun onPokemonItemClick(position: Int){
                val intent = Intent(this@PokedexActivity, PokemonCard::class.java).apply{
                    putExtra("pokemon",adapter.getpokemon(position))
                }
                startActivity(intent)
            }
        })

        //Atrapar un pokemon
        binding.catchPokemonBtn.setOnClickListener{
            var pokemonName = binding.pokemonCatchET.text.toString()
            if(pokemonName!=""){
                pokemonName = pokemonName.trim()
                pokemonName = pokemonName.lowercase()

                //cambio de corutina
                lifecycleScope.launch(Dispatchers.IO){

                    try{
                        pokemonName = pokemonName.trim()
                        val responseGET = HTTPSWebUtilDomi().GETRequest("${Constants.BASE_URL}api/v2/pokemon/${pokemonName}")
                        val jsonObject : JsonObject = Gson().fromJson(responseGET, JsonObject::class.java)

                        pokemonName = pokemonName.replaceFirstChar { it.uppercaseChar() }

                        val stats = jsonObject["stats"].toString()
                        val types = jsonObject["types"].toString()
                        val imgs = jsonObject["sprites"].toString()

                        var pokemonCatched = createNewPokemon(pokemonName, imgs, types, stats)

                        //Escribir pokemon en firebase
                        Firebase.firestore.collection("pokemon").add(pokemonCatched)

                        withContext(Dispatchers.Main){
                            Toast.makeText(this@PokedexActivity, "${pokemonName} has been catched", Toast.LENGTH_LONG).show()
                            addPokemonToPokemonRecyclerView()
                        }
                    }catch(e : Exception){

                        withContext(Dispatchers.Main){
                            Toast.makeText(this@PokedexActivity, "the pokemon doesn't exist! Enter a correct name", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }else{
                Toast.makeText(this, "The field shouldn't be empty", Toast.LENGTH_LONG).show()
            }
        }

    }


    //FUNCIÓN PARA CREAR AL POKEMON
    fun createNewPokemon(name : String, images :String, types: String, stats:String) : Pokemon{

        var typesArray = types.split("},")
        var statsArray = stats.split("},")

        var startImage = images.indexOf("front_default")
        var endImage = images.indexOf("front_female")

        //la ruta de la imagen debe ir desde el primer caracter de front_default(caracter 252) hasta antes del primer caracter de front_female (caracter 349)
        var imgURL = images.subSequence(startImage, endImage).toString()
        imgURL = imgURL.replace("front_default","") //eliminar la palabra front_default de la ruta
        imgURL = imgURL.replace("\":\"","") //eliminar el :" de la ruta
        imgURL = imgURL.replace("\",\"","") //elimnar la , de la ruta

        //ARREGLO DE STATS
        var i =0
        val stats : IntArray = intArrayOf(0,0,0,0,0,0)
        while(i<statsArray.size){
            var baseStat : String = statsArray[i]
            var start :Int = baseStat.indexOf(":")
            var end : Int = baseStat.indexOf(",")

            //guardo el base_stat (int) en la posición i del arreglo de stats
            stats[i] = baseStat.subSequence(start+1, end).toString().toInt()
            i++
        }

        //ARREGLO DE TYPES
        var j = 0
        var typeName :String =""
        while(j<typesArray.size){
            var typeOfPokemon :String= typesArray[j]
            var start :Int = typeOfPokemon.indexOf("name") //encontrar el indice donde empieza el "name" ya que es name":" y le sigue el tipo de pokemon (ejemplo : name":"normal")
            var end :Int= typeOfPokemon.indexOf("\",") //encontrar el indice donde hay ", ya que ahí acaba el tipo de pokemon (ejemplo= "name":"normal",) end: 33

            typeName = typeName+typeOfPokemon.subSequence(start+7, end).toString().replaceFirstChar { it.uppercaseChar() }
            typeName = typeName+"-"

            j++
        }
        typeName = typeName.subSequence(0, typeName.length-1).toString()

        val pokemonToCreate : Pokemon = Pokemon(
            imgURL,
            name,
            typeName,
            stats[0].toDouble(),
            stats[1].toDouble(),
            stats[2].toDouble(),
            stats[5].toDouble(),
            user.username,
            System.currentTimeMillis()
        )
        return pokemonToCreate
    }

    //Función para agregar a los pokemons al recycler view
    fun addPokemonToPokemonRecyclerView(){
        adapter.clearPokemonList()
        Firebase.firestore.collection("pokemon")
            .whereEqualTo("username", user.username)
            .orderBy("timeAdded", Query.Direction.ASCENDING)
            .get().addOnCompleteListener{ task->
                for(doc in task.result!!){
                    var pok = doc.toObject(Pokemon::class.java)
                    adapter.addPokemon(pok)
                    adapter.notifyDataSetChanged()
            }
            }
    }

    override fun onResume() {
        super.onResume()
        addPokemonToPokemonRecyclerView()
    }
}