package ee.ut.cs.lab5.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes WHERE id == :recipeId")
    fun  loadSingleRecipe(recipeId: Int): RecipeEntity

    @Query("SELECT name FROM recipes")
    fun loadRecipeTitles(): Array<String>

    @Query("SELECT * FROM recipes")
    fun loadRecipes(): Array<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecipes(vararg recipes: RecipeEntity)
}