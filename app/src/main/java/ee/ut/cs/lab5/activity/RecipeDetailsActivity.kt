package ee.ut.cs.lab5.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ee.ut.cs.lab5.R
import ee.ut.cs.lab5.databinding.ActivityRecipeDetailsBinding
import ee.ut.cs.lab5.room.LocalRecipesDb
import ee.ut.cs.lab5.room.RecipeEntity

class RecipeDetailsActivity : AppCompatActivity() {

    companion object { const val EXTRA_RECIPE_ID = "recipeId" }

    private lateinit var binding: ActivityRecipeDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup View Binding
        binding = ActivityRecipeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAndShowRecipe()
    }

    private fun loadAndShowRecipe() {
        // Get recipe ID from intent, load recipe details  from DB and show it in the UI
        val id = intent.getIntExtra(EXTRA_RECIPE_ID, -1)

        val loadedRecipe = getRecipeFromDb(id)
        loadedRecipe?.let { showRecipe(it) }
    }

    private fun getRecipeFromDb(id: Int): RecipeEntity? {
        val recipe = LocalRecipesDb.getInstance(this).getRecipeDao().loadSingleRecipe(id)
        return recipe
    }

    private fun showRecipe(recipe: RecipeEntity) {
        recipe.apply {
            binding.textviewDetailsTitle.text = name
            binding.textviewDetailsAuthor.text = author
            binding.textviewDetailsContent.text = contents
            if(recipe.path != null){
                val bmp = BitmapFactory.decodeFile(recipe.path)
                binding.imageView.setImageBitmap(bmp)
            }
            else {binding.imageView.setImageResource(R.drawable.notfound)}
        }
    }

}