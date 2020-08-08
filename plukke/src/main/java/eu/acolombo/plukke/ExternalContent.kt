package eu.acolombo.plukke

import android.content.ContentResolver
import android.content.ContentValues
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import java.io.Closeable

class ExternalContent(private val resolver: ContentResolver) : Closeable {

    constructor(activity: ComponentActivity) : this (activity.contentResolver)

    constructor(fragment: Fragment) : this (fragment.requireActivity().contentResolver)

    var uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())

    override fun close() {
        uri?.let { resolver.delete(it, null, null) }
    }

    fun clear() : Nothing? = close().let { return null }

}