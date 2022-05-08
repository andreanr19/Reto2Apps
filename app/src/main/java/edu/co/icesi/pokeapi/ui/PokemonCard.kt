package edu.co.icesi.pokeapi.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import edu.co.icesi.pokeapi.R
import edu.co.icesi.pokeapi.databinding.ActivityPokemonCardBinding
import edu.co.icesi.pokeapi.model.Pokemon
import edu.co.icesi.pokeapi.util.LoadImageTask

class PokemonCard : AppCompatActivity() {

    private lateinit var binding: ActivityPokemonCardBinding
    private lateinit var pokemon : Pokemon
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPokemonCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pokemon = (intent.extras?.get("pokemon") as Pokemon?)!!
        var img = LoadImageTask(binding.pokemonImage)
        img.execute(pokemon.image)

        binding.name.text = " ${binding.name.text}  ${pokemon.name} "
        binding.owner.text = " ${binding.owner.text}  ${pokemon.username} "
        binding.type.text = " ${binding.type.text}  ${pokemon.type} "
        binding.health.text = " ${binding.health.text}  ${pokemon.health} "
        binding.attack.text = " ${binding.attack.text}  ${pokemon.attack} "
        binding.defense.text = " ${binding.defense.text}  ${pokemon.defense} "
        binding.speed.text = " ${binding.speed.text}  ${pokemon.speed} "
        binding.timeAdded.text = " ${binding.timeAdded.text}  ${pokemon.timeAdded} "


    }
}

