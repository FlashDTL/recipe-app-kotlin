package ee.ut.cs.lab5.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "recipes")
data class RecipeEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String?,
    var contents: String?,
    var author: String?,
    var path: String?
    )

