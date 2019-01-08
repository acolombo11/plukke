package eu.acolombo.imagepicker.example

import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.load(uriString: String) {
    Picasso.get()
        .load(uriString)
        .into(this)
}

fun ImageView.load(uri: Uri) {
    Picasso.get()
        .load(uri)
        .into(this)
}