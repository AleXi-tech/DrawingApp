package com.furkan.drawingapp

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.furkan.drawingapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var permissionsResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>

    private lateinit var binding: ActivityMainBinding
    private var customProgressDialog: Dialog? = null
    private var drawingView: DrawingView? = null
    private var ibSelectBrushSize: ImageButton? = null
    private var ibRemoveLast: ImageButton? = null
    private var ibChangeColor: ImageButton? = null
    private var ibAddImage: ImageButton? = null
    private var ivBackground: ImageView? = null
    private var ibSave: ImageButton? = null
    private var flDrawingViewContainer: FrameLayout? = null

    companion object {
        private const val BRUSH_SIZE_SMALL = 4F
        private const val BRUSH_SIZE_MEDIUM = 8F
        private const val BRUSH_SIZE_LARGE = 16F
        private const val COLOR_BLACK = R.color.black
        private const val COLOR_BLUE = R.color.blue
        private const val COLOR_RED = R.color.red
        private const val COLOR_YELLOW = R.color.yellow
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawingView = binding.drawingView
        ibSelectBrushSize = binding.ibSelectBrushSize
        ibRemoveLast = binding.ibRemoveLast
        ibChangeColor = binding.ibChangeColor
        ibAddImage = binding.ibAddImage
        ivBackground = binding.ivBackground
        ibSave = binding.ibSave
        flDrawingViewContainer = binding.flDrawingViewContainer

        drawingView?.setSizeForBrush(BRUSH_SIZE_SMALL)

        ibSelectBrushSize?.setOnClickListener {
            showBrushSizeChooserDialog()
        }
        ibRemoveLast?.setOnClickListener {
            removeLastDraw()
        }
        ibChangeColor?.setOnClickListener {
            showColorChooserDialog()
        }
        ibAddImage?.setOnClickListener {
            requestStoragePermission()

        }
        ibSave?.setOnClickListener {
            if (isReadStorageAllowed()) {
                showProgressDialog()
                lifecycleScope.launch {
                    saveBitmapFile(getBitmapFromView(flDrawingViewContainer!!))
                }
            }
        }
    }

    private fun isReadStorageAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showRationaleDialog(
                "Drawing App",
                "Drawing App needs to access your external storage" +
                        ", please allow app to access your external storage from settings"
            )
        } else {
            permissionsResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        openGalleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    ivBackground?.setImageURI(result.data?.data)
                }
            }
        permissionsResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.forEach { permission ->
                val delimiter = if (permission.key.contains('_')) '_' else '.'
                if (permission.value) {
                    val pickIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    openGalleryLauncher.launch(pickIntent)
                } else {
                    Snackbar.make(
                        this, this.ibAddImage!!,
                        "Permission for ${permission.key.substringAfterLast(delimiter)} Denied." +
                                "Please allow app to access storage to use this feature",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun showColorChooserDialog() {
        val colorDialog = Dialog(this)
        colorDialog.setContentView(R.layout.dialog_choose_color)
        colorDialog.setTitle("Color : ")

        val colorBlackButton = colorDialog.findViewById<ImageButton>(R.id.ibColorBlack)
        val colorBlueButton = colorDialog.findViewById<ImageButton>(R.id.ibColorBlue)
        val colorRedButton = colorDialog.findViewById<ImageButton>(R.id.ibColorRed)
        val colorYellowButton = colorDialog.findViewById<ImageButton>(R.id.ibColorYellow)

        val colorButtons = listOf(
            colorBlackButton to COLOR_BLACK,
            colorBlueButton to COLOR_BLUE,
            colorRedButton to COLOR_RED,
            colorYellowButton to COLOR_YELLOW
        )
        colorButtons.forEach { (button, color) ->
            button.setOnClickListener {
                drawingView?.changeColor(ContextCompat.getColor(this, color))
                ibChangeColor?.setColorFilter(ContextCompat.getColor(this, color))
                colorDialog.dismiss()
            }
        }
        colorDialog.show()
    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size : ")
        val smallButton = brushDialog.findViewById<ImageButton>(R.id.ibSmallBrush)
        val mediumButton = brushDialog.findViewById<ImageButton>(R.id.ibMediumBrush)
        val largeButton = brushDialog.findViewById<ImageButton>(R.id.ibLargeBrush)

        val brushButtons = listOf(
            smallButton to BRUSH_SIZE_SMALL,
            mediumButton to BRUSH_SIZE_MEDIUM,
            largeButton to BRUSH_SIZE_LARGE
        )
        brushButtons.forEach { (button, brushSize) ->
            button.setOnClickListener {
                drawingView?.setSizeForBrush(brushSize)
                brushDialog.dismiss()
            }
        }
        brushDialog.show()
    }

    private fun removeLastDraw() {
        drawingView?.removeLastDraw()
    }

    private fun showRationaleDialog(
        title: String,
        message: String
    ) {
        val builder = AlertDialog.Builder(this)
        builder.run {
            setTitle(title)
            setMessage(message)
            setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.create().show()
        }

    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }
        view.draw(canvas)
        return returnedBitmap
    }

    private suspend fun saveBitmapFile(bitmap: Bitmap?): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (bitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val f = File(
                        externalCacheDir?.absoluteFile.toString() +
                                File.separator +
                                "DrawingApp_" +
                                System.currentTimeMillis() / 1000 +
                                ".png"
                    )
                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()

                    result = f.absolutePath
                    runOnUiThread {
                        dismissCustomDialog()
                        if (result.isNotEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "File successfully saved :$result",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Empty file !",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }
        return result
    }

    private fun showProgressDialog(){
        customProgressDialog = Dialog(this@MainActivity)
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
        customProgressDialog?.show()
    }
    private fun dismissCustomDialog() {
        if (customProgressDialog != null){
            customProgressDialog?.dismiss()
        }
        customProgressDialog = null
    }

}