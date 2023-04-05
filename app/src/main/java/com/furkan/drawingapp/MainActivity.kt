package com.furkan.drawingapp

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.furkan.drawingapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var permissionsResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>

    private lateinit var binding: ActivityMainBinding
    private var drawingView: DrawingView? = null
    private var ibSelectBrushSize: ImageButton? = null
    private var ibRemoveLast: ImageButton? = null
    private var ibChangeColor: ImageButton? = null
    private var ibAddImage: ImageButton? = null
    private var ivBackground: ImageView? = null

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
                    Manifest.permission.READ_EXTERNAL_STORAGE
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

}