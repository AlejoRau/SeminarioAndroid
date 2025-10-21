package unicen.seminario.model

data class Game(
    val id: Int,
    val name: String,
    val released: String?,
    val background_image: String?,
    val rating: Double?
)