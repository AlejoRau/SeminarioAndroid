package unicen.seminario.retrof

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameFilter(
    val genre: String? = null,
    val platform: String? = null,
    val ordering: String? = null,
    val page: Int = 1,
    val pageSize: Int = 15
) : Parcelable