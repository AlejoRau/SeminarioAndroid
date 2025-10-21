package unicen.seminario.model

data class GameResponse(
    val id: String,
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Game>
)


