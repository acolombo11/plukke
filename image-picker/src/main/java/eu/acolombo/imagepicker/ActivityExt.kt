package eu.acolombo.imagepicker

import androidx.fragment.app.FragmentActivity

fun <T> FragmentActivity.pick(picker: ImagePicker<T>, select: (T) -> Unit) =
    picker.pick(this, select)

//TODO Add Fragment