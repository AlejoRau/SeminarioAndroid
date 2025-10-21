package unicen.seminario

import android.app.AlertDialog
import unicen.seminario.retrof.GameFilter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import unicen.seminario.databinding.ActivityMainBinding
import unicen.seminario.kotlin.Search.SearchActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isLoadingNow = false
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: GamesAdapter

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val searchLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val filter = result.data?.getParcelableExtra("filter", GameFilter::class.java)
                if (filter != null) {
                    viewModel.updateFilters(filter)
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = GamesAdapter()
        binding.gamesContainer.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        viewModel.isLoading.observe(this) { isLoading ->
            isLoadingNow = isLoading
            binding.loadingContainer.loadingView.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.games.observe(this) { games ->
            adapter.submitList(games)
        }

        viewModel.noResultsEvent.observe(this) {
            Toast.makeText(this, "No se encontraron juegos", Toast.LENGTH_SHORT).show()
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.filters.observe(this) { filter ->
            viewModel.loadGames(filter)
        }

        binding.search.setOnClickListener {
            searchLauncher.launch(Intent(this, SearchActivity::class.java))
        }

        binding.btnLoadMore.setOnClickListener {
            viewModel.loadNextPage()
        }
        binding.sort.setOnClickListener {
            showOrderPopup()
        }

        binding.gamesContainer.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                val isAtBottom =
                    visibleItemCount + firstVisibleItemPosition >= totalItemCount &&
                            firstVisibleItemPosition >= 0 &&
                            totalItemCount >= visibleItemCount

                binding.btnLoadMore.visibility = if (isAtBottom) View.VISIBLE else View.GONE
            }
        })


    }
    private fun showOrderPopup() {
        val popup = PopupMenu(this, binding.sort)
        popup.menuInflater.inflate(R.menu.menu_order, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.order_name_asc -> viewModel.sortGamesBy("name")
                R.id.order_name_desc -> viewModel.sortGamesBy("-name")
                R.id.order_rating_asc -> viewModel.sortGamesBy("rating")
                R.id.order_rating_desc -> viewModel.sortGamesBy("-rating")
                R.id.order_date_asc -> viewModel.sortGamesBy("released")
                R.id.order_date_desc -> viewModel.sortGamesBy("-released")
            }
            true
        }

        popup.show()
    }

}
