package ee.ut.cs.lab5

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ee.ut.cs.lab5.room.LocalRecipesDb
import ee.ut.cs.lab5.room.RecipeEntity
import java.util.*

class RecipeViewModel(val app: Application): AndroidViewModel(app) {

    var recipeArray: Array<RecipeEntity> = arrayOf(
    )

    fun refresh(){
        val db = LocalRecipesDb.getInstance(app)
        val recipes = db.getRecipeDao().loadRecipes()
        recipeArray = recipes
    }

}