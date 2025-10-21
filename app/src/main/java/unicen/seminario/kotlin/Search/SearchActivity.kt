package unicen.seminario.kotlin.Search

import unicen.seminario.retrof.GameFilter
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

import unicen.seminario.MainViewModel
import unicen.seminario.databinding.ActivitySearchBinding
import kotlin.toString

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val genres = arrayOf("Todos", "action", "adventure", "rpg", "strategy", "shooter", "puzzle")
        val platforms = arrayOf("Todas", "PlayStation", "PC", "XBOX")
        val orders = arrayOf("Ninguno", "name", "-name", "released", "-released", "rating", "-rating")


        binding.genreSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genres).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.platformSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, platforms).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.orderSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, orders).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.applyButton.setOnClickListener {
            val genre = binding.genreSpinner.selectedItem as String
            val platform = binding.platformSpinner.selectedItem as String
            val ordering = binding.orderSpinner.selectedItem as String

            val newFilter = GameFilter(
                genre = when (genre) {
                    "Todos" -> null
                    "rpg" -> "role-playing-games-rpg"
                    else -> genre
                },
                platform = when (platform) {
                    "PC" -> "4"
                    "PlayStation" -> "18,187"
                    "XBOX" -> "1,186"
                    "Todas" -> null
                    else -> null
                },
                ordering = if (ordering == "Ninguno") null else ordering
            )

            val intent = Intent().putExtra("filter", newFilter)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}

