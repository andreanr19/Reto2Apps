package edu.co.icesi.pokeapi.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.co.icesi.pokeapi.R
import edu.co.icesi.pokeapi.model.Pokemon
import edu.co.icesi.pokeapi.util.LoadImage

class PokemonsAdapter : RecyclerView.Adapter<PokemonView>() {

    private val pokemons = ArrayList<Pokemon>()
    private lateinit var listenerPokemonItem : OnPokemonItemClickListenerInterface
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonView {


        //Inflater: XML -> View
        var inflater = LayoutInflater.from(parent.context)
        val row = inflater.inflate(R.layout.pokemonrow, parent, false)
        val pokemonView = PokemonView(row, listenerPokemonItem)
        return pokemonView
    }

    override fun onBindViewHolder(skeleton: PokemonView, position: Int) {
        val pokemon = pokemons[position]
        skeleton.pokemonNameRow.text = pokemon.name

        var img = LoadImage(skeleton.pokemonImageRow)
        img.execute(pokemon.image)

    }

    override fun getItemCount(): Int {
        return pokemons.size
    }

    fun clearPokemonList(){
        pokemons.clear()
    }

    fun getpokemon(position: Int) : Pokemon{
        return pokemons.get(position)
    }

    fun addPokemon(newPokemon : Pokemon){
        pokemons.add(newPokemon)

    }

    fun setOnPokemonItemClickListener(lstner : OnPokemonItemClickListenerInterface){
        listenerPokemonItem = lstner
    }

    interface OnPokemonItemClickListenerInterface{
        fun onPokemonItemClick(position: Int)
    }
}