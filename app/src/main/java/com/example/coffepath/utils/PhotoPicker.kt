package com.example.coffepath.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.coffepath.R
import coil.compose.rememberAsyncImagePainter

@Composable
fun PhotoPicker(
    selectedImageUri: Uri?,
    onImageSelected: (Uri) -> Unit,
    onImageRemoved: () -> Unit,
    modifier: Modifier = Modifier,
    showPreview: Boolean = true,
    enabled: Boolean = true
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val handleUri: (Uri) -> Unit = { uri ->
        try {
            PhotoUtils.loadBitmapFromUri(context, uri, 1024, 1024)?.let { bitmap ->
                val corrected = PhotoUtils.correctImageOrientation(context, uri, bitmap)
                val compressed = PhotoUtils.compressBitmap(corrected, 800, 800)
                PhotoUtils.saveBitmapToFile(context, compressed, "profile_${System.currentTimeMillis()}")?.let {
                    onImageSelected(it)
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let { handleUri(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) tempCameraUri?.let { onImageSelected(it) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        if (results.values.all { it }) showDialog = true else showPermissionRationale = true
    }

    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (showPreview && selectedImageUri != null) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))) {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(onClick = onImageRemoved, modifier = Modifier.align(Alignment.TopEnd)) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                }
            }
        } else {
            OutlinedCard(
                onClick = { showDialog = true },
                enabled = enabled,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddAPhoto, null, Modifier.size(48.dp), MaterialTheme.colorScheme.primary)
                    Text(text = stringResource(R.string.tap_to_add_photo), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { stringResource(R.string.choose_photo_source) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SourceButton(Icons.Default.CameraAlt, stringResource(R.string.take_photo)) {
                        showDialog = false
                        if (PermissionUtils.hasCameraPermissions(context)) {
                            PhotoUtils.createImageUri(context, "cam_${System.currentTimeMillis()}.jpg")?.let {
                                tempCameraUri = it
                                cameraLauncher.launch(it)
                            }
                        } else permissionLauncher.launch(PermissionUtils.CAMERA_PERMISSIONS)
                    }
                    SourceButton(Icons.Default.PhotoLibrary, stringResource(R.string.choose_from_gallery)) {
                        showDialog = false
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showDialog = false }) { stringResource(R.string.cancel) } }
        )
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            confirmButton = { TextButton(onClick = { showPermissionRationale = false }) { Text("OK") } },
            title = { stringResource(R.string.permission_required) },
            text = { stringResource(R.string.permission_explanation) }
        )
    }
}

@Composable
fun SourceButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}

