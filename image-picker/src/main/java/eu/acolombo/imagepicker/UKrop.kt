package eu.acolombo.imagepicker

import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropFragment

val UCropFragment.UCropResult.data
get() = UCrop.getOutput(mResultData)