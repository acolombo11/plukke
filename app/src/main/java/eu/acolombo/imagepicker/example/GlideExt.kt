package eu.acolombo.imagepicker.example

import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.load(bitmap: Bitmap) {
    Glide.with(context).load(bitmap).into(this)
}

fun ImageView.load(uri: Uri) {
    Glide.with(context).load(uri).into(this)
}

fun ImageView.load(uri: String?) {
    if (!uri.isNullOrEmpty()) Glide.with(context).load(uri).into(this)
}

fun ImageView.load(resource: Int) {
    if (resource != 0) Glide.with(context).load(resource).into(this)
}

fun ImageView.load(any: Any?) {
    Glide.with(context).load(any).into(this)
}