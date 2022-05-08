package edu.co.icesi.pokeapi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.co.icesi.pokeapi.R

class PokemonCard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_card)
    }
}