
package eu.acolombo.plukke.example.utils

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.ColorUtils.setAlphaComponent
import eu.acolombo.plukke.example.R
import eu.acolombo.plukke.example.utils.CodeSequenceBuilder.Code.Text

@Suppress("MemberVisibilityCanBePrivate", "unused")
class CodeSequenceBuilder(private val context: Context) {

    private val defaultColor = TextView(context).textColors.defaultColor

    sealed class Code(value: CharSequence, val alpha: Int) : SpannableString(value) {
        class Text(text: CharSequence) : Code(text, 0xFF/2)
        class Comment(text: CharSequence) : Code(text, 0xFF/3)
        class Highlight(text: CharSequence) : Code(text, 0xFF)
        sealed class Keyword(text: CharSequence, @ColorRes val color: Int, val style: Int? = null) : Code(text, 0xFF) {
            class Extension(text: CharSequence) : Keyword(text,
                R.color.colorExtension, Typeface.ITALIC)
            class Variable(text: CharSequence) : Keyword(text,
                R.color.colorVariable
            )
            class Todo(text: CharSequence) : Keyword(text,
                R.color.colorTodo, Typeface.ITALIC)
            class Colored(text: CharSequence, @ColorRes color: Int) : Keyword(text, color)
        }
    }

    private val builder = SpannableStringBuilder()
    private var currentIndentation = 0

    fun build(): CharSequence = builder

    fun clear() = builder.clear().also { currentIndentation = 0 }

    fun indent(vararg text: CharSequence) = apply {
        currentIndentation++
        appendLn(*text)
    }

    fun recess(vararg text: CharSequence) = apply {
        currentIndentation--
        appendLn(*text)
    }

    fun append(vararg text: CharSequence) = apply {
        text.forEach {
            builder.append(when (it) {
                is Code.Keyword -> it.apply {
                    setSpan(ForegroundColorSpan(getColor(context, color)), 0, length, 0)
                    style?.let { setSpan(StyleSpan(style), 0, length, 0) }
                }
                else -> (it as? Code
                    ?: Text(it)).apply {
                    setSpan(ForegroundColorSpan(setAlphaComponent(defaultColor, alpha)), 0, length, 0)
                }
            })
        }
    }

    fun appendLn(vararg text: CharSequence) = apply {
        builder.appendln()
        repeat(currentIndentation) { builder.append("\t\t\t") }
        append(*text)
    }

}





