package eu.acolombo.plukke

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import eu.acolombo.plukke.ActivityResultContracts.Choose
import eu.acolombo.plukke.ActivityResultContracts.PickImage
import eu.acolombo.plukke.ActivityResultContracts.TakePhoto

private fun ComponentActivity.doWithPermission(
    permission: String = WRITE_EXTERNAL_STORAGE,
    action: () -> Unit
) {
    // Some devices don't need any permission, so we'll just try to do what we want
    // If we can't (catch) we do it the proper way, requesting permission if needed
    try {
        action()
    } catch (e: SecurityException) {
        if (checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) action()
            }.launch(permission)
        } else {
            action()
        }
    }
}

fun ComponentActivity.pickImage(onResult: (Uri) -> Unit) = doWithPermission {
    val photo = TakePhoto(this)
    val image = PickImage()

    registerForActivityResult(Choose()) choose@{ result ->
        onResult(
            if (result.resultCode == Activity.RESULT_OK) result.data.let { intent ->
                intent?.data ?: photo.uri ?: return@choose
            } else return@choose
        )
    }.launch(listOf(photo, image))
}

fun Fragment.pickImage(onPick: (Any?) -> Unit) = activity?.pickImage(onPick)

fun ComponentActivity.takePicture(onResult: (Uri) -> Unit) = doWithPermission {
    contentResolver.insert(EXTERNAL_CONTENT_URI, ContentValues())?.let { uri ->
        registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok) onResult(uri)
        }.launch(uri)
    }
}

fun Fragment.takePicture(onTake: (Uri) -> Unit) = activity?.takePicture(onTake)