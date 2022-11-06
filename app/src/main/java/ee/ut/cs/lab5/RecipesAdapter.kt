package ee.ut.cs.lab5

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ee.ut.cs.lab5.room.RecipeEntity

class RecipesAdapter(
    var data: Array<RecipeEntity> =  arrayOf(),
    private var listener: RecipeClickListener
    ) : RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() {

    fun interface RecipeClickListener {
        fun onRecipeClick(recipe: RecipeEntity)
    }


    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun getItemCount(): Int = data.size


    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = data[position]
        val bmp = BitmapFactory.decodeFile(recipe.path)

        holder.itemView.apply {
            this.findViewById<TextView>(R.id.recipeTitleTextView).text = recipe.name
            this.findViewById<TextView>(R.id.recipeAuthorNameTextView).text = recipe.author
            if (recipe.path != null){
                this.findViewById<ImageView>(R.id.imageView5).setImageBitmap(bmp)
            }else{this.findViewById<ImageView>(R.id.imageView5).setImageResource(R.drawable.notfound)}
            setOnClickListener { listener.onRecipeClick(recipe) }
        }
    }
}