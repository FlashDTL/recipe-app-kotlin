package ee.ut.cs.lab5.activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import ee.ut.cs.lab5.RecipeViewModel
import ee.ut.cs.lab5.RecipesAdapter
import ee.ut.cs.lab5.databinding.ActivityMainBinding
import ee.ut.cs.lab5.room.LocalRecipesDb
import ee.ut.cs.lab5.room.RecipeEntity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recipesAdapter: RecipesAdapter
    val model: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(LocalRecipesDb.getInstance(this).getRecipeDao().loadRecipes().isNullOrEmpty()){
            generateTestEntities()
        }

        setupRecyclerView()

        binding.buttonNewrecipe.setOnClickListener { openNewRecipeActivity() }
    }

    private fun generateTestEntities() {
        val recipe1 = RecipeEntity(0, "Lasagna", "Spread a thin layer of pasta sauce in the bottom of a baking dish.\n" +
                "Make a layer of cooked lasagna noodles.\n" +
                "Spread an even layer of the ricotta cheese mixture.\n" +
                "Spread an even layer of meat sauce.\n" +
                "Repeat those layers two times.\n" +
                "Top it with a final layer of noodles, sauce, mozzarella, and parmesan cheese.", "Author: Paul", null)
        val recipe2 = RecipeEntity(0, "Pizza", "In a large mixing bowl, combine flours and salt.\n" +
                "In a small mixing bowl, stir together 200 grams (a little less than 1 cup) lukewarm tap water, the yeast and the olive oil, then pour it into flour mixture. Knead with your hands until well combined, approximately 3 minutes, then let the mixture rest for 15 minutes.\n" +
                "Knead rested dough for 3 minutes. Cut into 2 equal pieces and shape each into a ball. Place on a heavily floured surface, cover with dampened cloth, and let rest and rise for 3 to 4 hours at room temperature or for 8 to 24 hours in the refrigerator. (If you refrigerate the dough, remove it 30 to 45 minutes before you begin to shape it for pizza.)\n" +
                "To make pizza, place each dough ball on a heavily floured surface and use your fingers to stretch it, then your hands to shape it into rounds or squares. Top and bake.", "Author: Steven", null)

        LocalRecipesDb.getInstance(this).getRecipeDao().insertRecipes(recipe1)
        LocalRecipesDb.getInstance(this).getRecipeDao().insertRecipes(recipe2)
    }

    override fun onResume(){
        super.onResume()

        model.refresh()
        recipesAdapter.data = model.recipeArray
        recipesAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        val recipeClickListener = RecipesAdapter.RecipeClickListener { p -> openRecipeDetailsActivity(p) }
        recipesAdapter = RecipesAdapter(model.recipeArray, recipeClickListener)
        binding.recyclerviewRecipelist.adapter = recipesAdapter
        binding.recyclerviewRecipelist.layoutManager = LinearLayoutManager(this)
    }


    private fun openNewRecipeActivity() {
        startActivity(Intent(this, NewRecipeActivity::class.java))
    }

    private fun openRecipeDetailsActivity(recipe: RecipeEntity) {
        val intent = Intent(this, RecipeDetailsActivity::class.java)
        intent.putExtra("recipeId", recipe.id)
        startActivity(intent)
    }
}