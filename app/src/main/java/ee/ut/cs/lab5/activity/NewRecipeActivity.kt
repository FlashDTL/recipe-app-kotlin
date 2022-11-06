package ee.ut.cs.lab5.activity
// Idea sources: https://www.youtube.com/watch?v=xsUnbQEfJ6I

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import ee.ut.cs.lab5.R
import ee.ut.cs.lab5.databinding.ActivityNewRecipeBinding
import ee.ut.cs.lab5.room.LocalRecipesDb
import ee.ut.cs.lab5.room.RecipeEntity
import java.io.File
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewRecipeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewRecipeBinding
    private lateinit var pathToPhoto: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCameraButton()

        setupSaveButton()
    }

    //______________________________________________________________________________________________
        //CAMERA


    private fun setupCameraButton() {
        binding.camerabtn.setOnClickListener {
            takeAPhoto()
        }
    }

    private fun takeAPhoto() {
        //First of all we have to check the permissions:
        if (hasCameraPermission() == PERMISSION_GRANTED && hasExternalStoragePermisson() == PERMISSION_GRANTED) {
            //User already gave the permissions. Invoke camera:
        invokeCamera()
        }else{
            // User did not give the permissions. Request it:
            requestMultiplePermissionsLauncher.launch(arrayOf(
                Manifest.permission.CAMERA,                 //popup
                Manifest.permission.WRITE_EXTERNAL_STORAGE  //popup
            ))

        }
    }



    fun hasCameraPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
    fun hasExternalStoragePermisson() = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)


    private val requestMultiplePermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {     //accepts as this parameter an array of permissions, that user clicked, that we want to request
    listOfPermissionsUserGave ->
        var permissionGranted = false
        // Let's check if ALL elements have permissions:
        listOfPermissionsUserGave.forEach {
            if (it.value == true) {
                permissionGranted = true
            } else {
                permissionGranted = false
                return@forEach  // Exit loop as soon as we get one denied permission.
            }
        }
        //Let's check if we got ALL permissions:
        if (permissionGranted == true) {
            // Yes! Now invoke camera:
            invokeCamera()
        }else{
            Toast.makeText(this, "Unable to work without permissions", Toast.LENGTH_SHORT).show()   //Here we also can extract string to put into strings.xml
        }
    }

    private fun invokeCamera() {
        Log.i("NewRecipeActivity", "Camera invoked!")

        // Define the file using a custom function getPhotoFile() (see below)
        val photoFile = getPhotoFile()
        try {
            val fileUri = FileProvider.getUriForFile(this, "ee.ut.cs.lab5.fileprovider", photoFile)

            Log.i("NewRecipeActivity", "photoFilePath: ${photoFile.path}")                  //photoFilePath: /storage/emulated/0/Android/data/ee.ut.cs.lab5/files/Pictures/20221030_073107
            Log.i("NewRecipeActivity", "fileUri: $fileUri")                                 //fileUri: content://ee.ut.cs.lab5.fileprovider/images/20221030_073107
            Log.i("NewRecipeActivity", "fileUriPath: ${fileUri.path}")                      //fileUriPath: /images/20221030_073107

            pathToPhoto = photoFile.path

//            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

            getCameraPhoto.launch(fileUri) // use a registerForActivityResult(.. ) launcher

        }catch (e:Exception){
            Log.e("NewRecipeActivity", "${e.message}")
        }
    }

    /**
     * Creating file, where photo will evetially be stored.
     * Creating the storage directory if it does not exist:*/
    fun getPhotoFile(): File {
        // We will use a timestamp in our file name to ensure uniqueness:
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        // Get safe storage directory for photos. Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir: File = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("NewRecipeActivity", "failed to create directory")
        }
        return File(mediaStorageDir.path + File.separator + timestamp)
    }

    /**
     * Actually opens camera and waits for a photo to be taken.
     */
    private val getCameraPhoto = registerForActivityResult(ActivityResultContracts.TakePicture()){
            success ->
        if (success) {
            Log.i("NewRecipeActivity", "Photo is saved here: $pathToPhoto")
            val imageView = findViewById<ImageView>(R.id.recentlyTakenPhoto)
            val bmpFull = BitmapFactory.decodeFile(pathToPhoto)
// Scaling down photo size:
            val ratio = bmpFull.width.toDouble() / bmpFull.height
            val bmpScaledDown = Bitmap.createScaledBitmap(bmpFull, (800*ratio).toInt(), 800, false)
            imageView.setImageBitmap(bmpScaledDown)

        }else{
            Log.e( "NewRecipeActivity", "Photo was NOT saved: $pathToPhoto")
        }
    }



    //_____________________________________________________________________________________________
        // SAVE RECIPE


    private fun setupSaveButton() {

        binding.buttonSave.setOnClickListener {
            //Clearing path to photo from previous usage:
//            pathToPhoto = ""
            // Fetch the values from UI user input
            val newRecipe = getUserEnteredRecipe()
            // Store them in DB
            if (newRecipe != null){
                saveRecipeToDb(newRecipe)
                finish()
            } else {
                Toast.makeText(this, "Some fields empty or date invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveRecipeToDb(newRecipe: RecipeEntity) {
        LocalRecipesDb.getInstance(this).getRecipeDao()
            .insertRecipes(newRecipe)
    }

    private fun getUserEnteredRecipe(): RecipeEntity? {
        val editTexts = listOf(
            binding.titleEditText,
            binding.contentEditText,
            binding.authorEditText,
        )

        val allEditTextsHaveContent = editTexts.all { !TextUtils.isEmpty(it.text) }

        if (!allEditTextsHaveContent){
            return null // Input is not valid
        }
        if (!this::pathToPhoto.isInitialized){
            val newRecipe = RecipeEntity(
                0,
                binding.titleEditText.text.toString(),
                binding.contentEditText.text.toString(),
                "Author: " + binding.authorEditText.text.toString(),
                null
            )
            return newRecipe

        }
        else{
            val newRecipe = RecipeEntity(
                0,
                binding.titleEditText.text.toString(),
                binding.contentEditText.text.toString(),
                "Author: " + binding.authorEditText.text.toString(),
                pathToPhoto
            )
            return newRecipe
        }
    }
}