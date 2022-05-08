package edu.co.icesi.pokeapi.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.co.icesi.pokeapi.R


//este es nuestro ViewHolder
class PokemonView(itemView: View, listener : PokemonsAdapter.OnPokemonItemClickListenerInterface) : RecyclerView.ViewHolder(itemView) {

    //Identificar los UI components
    var pokemonNameRow : TextView = itemView.findViewById(R.id.pokemonNameRow)
    var pokemonImageRow : ImageView = itemView.findViewById(R.id.pokemonImageRow)

    init{
        itemView.setOnClickListener{
            listener.onPokemonItemClick(adapterPosition)
        }
    }
}