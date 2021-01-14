package eu.acolombo.plukke

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import eu.acolombo.plukke.PlukkeActivityResultContracts.Choose
import eu.acolombo.plukke.PlukkeActivityResultContracts.PickImage
import eu.acolombo.plukke.PlukkeActivityResultContracts.TakePhoto
import java.io.Closeable

class Plukke(
    private val componentActivity: ComponentActivity,
    clearOnDestroy: Boolean = true
) : LifecycleObserver, Closeable {

    private class ExternalContent(private val resolver: ContentResolver) : Closeable {

        var uri = resolver.insert(EXTERNAL_CONTENT_URI, ContentValues())

        override fun close() {
            uri?.let { resolver.delete(it, null, null) }
        }

    }

    private val exc = ExternalContent(componentActivity.contentResolver)

    val uri = exc.uri

    fun pickImage(onResult: (Uri) -> Unit) = componentActivity.doWithWritePermission {
        val photo: TakePhoto? = uri?.let { TakePhoto(it) }
        val pick = PickImage()

        componentActivity.registerForActivityResult(Choose()) { result ->
            if (result.resultCode == Activity.RESULT_OK) onResult(
                result?.data?.data ?: photo?.uri ?: exc.close()
                    .run { return@registerForActivityResult }
            ) else exc.close()
        }.launch(listOfNotNull(photo, pick))
    }

    fun takePicture(onResult: (Uri) -> Unit) = componentActivity.doWithWritePermission {
        uri?.let { uri ->
            componentActivity.registerForActivityResult(TakePicture()) { ok ->
                if (ok) onResult(uri) else exc.close()
            }.launch(uri)
        }
    }

    var autoClear: Boolean = false
        set(value) {
            field = value
            componentActivity.lifecycle.removeObserver(this)
            if (value) componentActivity.lifecycle.addObserver(this)
        }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun close() = exc.close()

    init {
        autoClear = clearOnDestroy
    }

    private fun ComponentActivity.doWithWritePermission(action: ComponentActivity.() -> Unit) {
        // Some devices don't need WRITE_EXTERNAL_STORAGE permission to be granted, so we'll just try to do what we want
        // If we can't (catch) we do it the proper way, requesting permission if needed
        // The try & catch is here to improve first time experience on some devices
        try {
            action()
        } catch (e: SecurityException) {
            doWithPermission(WRITE_EXTERNAL_STORAGE, action)
        }
    }

}