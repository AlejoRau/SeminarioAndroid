package unicen.seminario.retrof

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import unicen.seminario.model.GameResponse

interface GameApi {

    @GET("games")
    suspend fun getGames(
        @Query("key") key: String,
        @Query("page") page: Int? = 1,
        @Query("platforms") platforms: String? = null,
        @Query("genres") genres: String? = null,
        @Query("ordering") ordering: String? = null,
        @Query("search") search: String? = null
    ): Response<GameResponse>
}