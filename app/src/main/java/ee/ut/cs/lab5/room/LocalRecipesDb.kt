package ee.ut.cs.lab5.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


    @Database(entities = [ RecipeEntity::class ], version = 4)
    abstract  class LocalRecipesDb : RoomDatabase() {

        companion object {
            private lateinit var RecipeDb : LocalRecipesDb

            @Synchronized
            fun getInstance(context: Context) : LocalRecipesDb {

                if (!this::RecipeDb.isInitialized) {
                     RecipeDb = Room.databaseBuilder(
                        context, LocalRecipesDb::class.java, "myRecipes")
                        .fallbackToDestructiveMigration() // each time schema changes, data is lost!
                        .allowMainThreadQueries() // if possible, use background thread instead
                        .build()
                }
                return RecipeDb

            }
        }


        abstract fun getRecipeDao(): RecipeDao
    }