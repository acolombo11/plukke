package eu.acolombo.plukke

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment

fun ComponentActivity.pickImage(onPick: (Uri) -> Unit) = Plukke(this).also { it.pickImage(onPick) }

fun ComponentActivity.takePicture(onPick: (Uri) -> Unit) = Plukke(this).also { it.takePicture(onPick) }

fun Fragment.pickImage(onPick: (Uri) -> Unit) = activity?.pickImage(onPick)

fun Fragment.takePicture(onTake: (Uri) -> Unit) = activity?.takePicture(onTake)

fun ComponentActivity.doWithPermission(
    permission: String,
    action: ComponentActivity.() -> Unit
) = if (checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
    registerForActivityResult(RequestPermission()) {
        if (it) action()
    }.launch(permission)
} else {
    action()
}

fun Fragment.doWithPermission(
    permission: String,
    action: ComponentActivity.() -> Unit
) = activity?.doWithPermission(permission, action)