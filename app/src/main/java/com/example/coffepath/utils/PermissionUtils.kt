package com.example.coffepath.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

object PermissionUtils {
    val CAMERA_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    val GALLERY_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
    fun hasCameraPermissions(context: Context): Boolean {
        return hasPermissions(context, CAMERA_PERMISSIONS)
    }
    fun hasGalleryPermissions(context: Context): Boolean {
        return hasPermissions(context, GALLERY_PERMISSIONS)
    }
    fun createPermissionLauncher(
        activity: ComponentActivity,
        onResult: (Map<String, Boolean>) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            onResult(permissions)
        }
    }
}

class PermissionManager(
    private val context: Context
) {
    fun checkCameraPermission(): PermissionStatus {
        return if (PermissionUtils.hasCameraPermissions(context)) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.NotGranted
        }
    }
    fun checkGalleryPermission(): PermissionStatus {
        return if (PermissionUtils.hasGalleryPermissions(context)) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.NotGranted
        }
    }
}

sealed class PermissionStatus {
    object Granted : PermissionStatus()
    object NotGranted : PermissionStatus()
    object Denied : PermissionStatus()
}