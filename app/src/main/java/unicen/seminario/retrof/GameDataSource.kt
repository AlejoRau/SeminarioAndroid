package unicen.seminario.retrof

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import unicen.seminario.model.Game
import javax.inject.Inject

class GameDataSource @Inject constructor(
    private val gameApi: GameApi
) {
    suspend fun getGames(gameFilter: GameFilter): List<Game>? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("GameDataSource", "Requesting games with filter: $gameFilter")
                val response = gameApi.getGames(
                    key = "9cf2da0644a84b488e395af03cd9d27a",
                    page = gameFilter.page,
                    platforms = gameFilter.platform,
                    genres = gameFilter.genre,
                    ordering = gameFilter.ordering,

                )
                Log.d("GameDataSource", "Response Code: ${response.code()}")
                Log.d(
                    "GameDataSource",
                    "Response Body: ${response.body()?.results ?: "No results"}"
                )

                if (response.isSuccessful) {
                    response.body()?.results
                } else {
                    Log.e("GameDataSource", "Error response: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("GameDataSource", "Exception during API call: ${e.message}", e)
                null
            }
        }
    }
}