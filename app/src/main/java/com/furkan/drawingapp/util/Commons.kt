package com.furkan.drawingapp.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat

class Commons {
    companion object{
        fun isReadStorageAllowed(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        fun shareImage(uri: Uri, onStartActivity: (Intent) -> Unit) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.flags = Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            shareIntent.type = "image/png"
            onStartActivity(Intent.createChooser(shareIntent, "Share"))
        }
    }
}