package eu.acolombo.plukke.example

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.load(uri: Uri) {
    Glide.with(context).load(uri).into(this)
}

fun ImageView.load(any: Any?) {
    Glide.with(context).load(any).into(this)
}