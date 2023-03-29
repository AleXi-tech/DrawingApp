package com.furkan.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null

    private var ibSelectBrushSize: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawingView)
        ibSelectBrushSize = findViewById(R.id.ibSelectBrushSize)

        drawingView?.setSizeForBrush(2F)

        ibSelectBrushSize?.setOnClickListener {
            showBrushSizeChooserDialog()
        }


    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Brush Size : ")
        val smallButton = brushDialog.findViewById<ImageButton>(R.id.ibSmallBrush)
        val mediumButton = brushDialog.findViewById<ImageButton>(R.id.ibMediumBrush)
        val largeButton = brushDialog.findViewById<ImageButton>(R.id.ibLargeBrush)

        smallButton.setOnClickListener {
            drawingView?.setSizeForBrush(4F)
            brushDialog.dismiss()
        }
        mediumButton.setOnClickListener {
            drawingView?.setSizeForBrush(8F)
            brushDialog.dismiss()
        }
        largeButton.setOnClickListener {
            drawingView?.setSizeForBrush(16F)
            brushDialog.dismiss()
        }

        brushDialog.show()
    }
}