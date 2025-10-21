package unicen.seminario

import unicen.seminario.retrof.GameDataSource
import unicen.seminario.retrof.GameFilter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import unicen.seminario.model.Game
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gameDataSource: GameDataSource
) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _games = MutableLiveData<List<Game>>(emptyList())
    val games: LiveData<List<Game>> get() = _games

    private val _filters = MutableLiveData(GameFilter())
    val filters: LiveData<GameFilter> get() = _filters

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> get() = _error


    private val _noResultsEvent = MutableLiveData<Unit>()
    val noResultsEvent: LiveData<Unit> get() = _noResultsEvent

    private var currentPage = 1
    private var hasNextPage = true
    private var lastFilter = GameFilter()

    fun loadGames(filter: GameFilter = lastFilter, reset: Boolean = false) {
        viewModelScope.launch {
            if (reset) {
                currentPage = 1
                hasNextPage = true
                lastFilter = filter
                _games.postValue(emptyList())
            }

            if (!hasNextPage) return@launch

            _isLoading.postValue(true)

            try {
                val pagedFilter = filter.copy(page = currentPage)
                val result = gameDataSource.getGames(pagedFilter) ?: emptyList()

                val updatedList = if (reset) result else (_games.value ?: emptyList()) + result
                _games.postValue(updatedList)

                hasNextPage = result.size >= pagedFilter.pageSize
                if (hasNextPage) currentPage++


                if (updatedList.isEmpty()) {
                    _noResultsEvent.postValue(Unit)
                }

            } catch (e: Exception) {
                _error.postValue("Error al cargar juegos: ${e.message}")
                if (reset) _games.postValue(emptyList())
                hasNextPage = false
            }
                _isLoading.postValue(false)

        }
    }

    fun updateFilters(newFilter: GameFilter) {
        if (_filters.value != newFilter) {
            _filters.value = newFilter
            loadGames(newFilter, reset = true)
        }
    }

    fun loadNextPage() {
        loadGames(lastFilter, reset = false)
    }

    fun sortGamesBy(order: String) {
        val currentList = _games.value ?: return

        val sortedList = when (order) {
            "name" -> currentList.sortedBy { it.name }
            "-name" -> currentList.sortedByDescending { it.name }
            "rating" -> currentList.sortedBy { it.rating ?: 0.0 }
            "-rating" -> currentList.sortedByDescending { it.rating ?: 0.0 }
            "released" -> currentList.sortedBy { it.released ?: "" }
            "-released" -> currentList.sortedByDescending { it.released ?: "" }
            else -> currentList
        }

        _games.postValue(sortedList)
    }

}