package com.furkan.drawingapp

import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {

    private var drawingView: DrawingView? = null

    private var ibSelectBrushSize: ImageButton? = null

    private var ibRemoveLast: ImageButton? = null
    private var ibChangeColor: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawingView = findViewById(R.id.drawingView)
        ibSelectBrushSize = findViewById(R.id.ibSelectBrushSize)
        ibRemoveLast = findViewById(R.id.ibRemoveLast)
        ibChangeColor = findViewById(R.id.ibChangeColor)

        drawingView?.setSizeForBrush(2F)

        ibSelectBrushSize?.setOnClickListener {
            showBrushSizeChooserDialog()
        }

        ibRemoveLast?.setOnClickListener {
            removeLastDraw()
        }

        ibChangeColor?.setOnClickListener {
            showColorChooserDialog()
        }


    }

    private fun showColorChooserDialog() {
        val colorDialog = Dialog(this)
        colorDialog.setContentView(R.layout.dialog_choose_color)
        colorDialog.setTitle("Brush Size : ")
        val colorBlackButton = colorDialog.findViewById<ImageButton>(R.id.ibColorBlack)
        val colorBlueButton = colorDialog.findViewById<ImageButton>(R.id.ibColorBlue)
        val colorRedButton = colorDialog.findViewById<ImageButton>(R.id.ibColorRed)
        val colorYellowButton = colorDialog.findViewById<ImageButton>(R.id.ibColorYellow)

        colorBlackButton.setOnClickListener {
            drawingView?.changeColor(Color.BLACK)
            colorDialog.dismiss()
        }
        colorBlueButton.setOnClickListener {
            drawingView?.changeColor(Color.BLUE)
            colorDialog.dismiss()
        }
        colorRedButton.setOnClickListener {
            drawingView?.changeColor(Color.RED)
            colorDialog.dismiss()
        }
        colorYellowButton.setOnClickListener {
            drawingView?.changeColor(Color.YELLOW)
            colorDialog.dismiss()
        }
        colorDialog.show()
    }

    private fun removeLastDraw() {
        drawingView?.removeLastDraw()
    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("Color : ")
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