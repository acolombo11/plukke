package eu.acolombo.plukke

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import eu.acolombo.plukke.ActivityResultContracts.Choose
import eu.acolombo.plukke.ActivityResultContracts.PickImage
import eu.acolombo.plukke.ActivityResultContracts.TakePhoto

fun ComponentActivity.pickImage(onResult: (Uri) -> Unit) = doWithPermission {
    val exc = ExternalContent(contentResolver)

    val photo = exc.uri?.let { TakePhoto(it) }
    val pick = PickImage()

    registerForActivityResult(Choose()) choose@{ result ->
        if (result.resultCode == RESULT_OK) onResult(
            photo?.output
                ?: exc.clear()
                ?: result?.data?.data
                ?: return@choose
        ) else exc.close()
    }.launch(listOfNotNull(photo, pick))
}

fun ComponentActivity.takePicture(onResult: (Uri) -> Unit) = doWithPermission {
    val exc = ExternalContent(contentResolver)

    exc.uri?.let { uri ->
        registerForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
            if (ok) onResult(uri) else exc.close()
        }.launch(uri)
    }
}

fun Fragment.pickImage(onPick: (Uri) -> Unit) =
    activity?.pickImage(onPick)

fun Fragment.takePicture(onTake: (Uri) -> Unit) =
    activity?.takePicture(onTake)

private fun ComponentActivity.doWithPermission(
    permission: String = WRITE_EXTERNAL_STORAGE,
    action: () -> Unit
) {
    // Some devices don't need WRITE_EXTERNAL_STORAGE permission to be granted, so we'll just try to do what we want
    // If we can't (catch) we do it the proper way, requesting permission if needed
    // The try & catch is there to improve first time experience on some devices, after the first time the try will succeed anyways
    try {
        action()
    } catch (e: SecurityException) {
        if (checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
            registerForActivityResult(RequestPermission()) {
                if (it) action()
            }.launch(permission)
        } else {
            action()
        }
    }
}