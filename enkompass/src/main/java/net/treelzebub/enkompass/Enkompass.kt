package net.treelzebub.enkompass

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.annotation.StyleRes
import android.support.annotation.VisibleForTesting
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.TextView

/**
 * Created by Tre Murillo on 2/3/18
 */


/**
 * Kotlin-style builder for applying various spans to one substring.
 * Example:
 *     "Woohoo!".enkompass("hoo!") {
 *         boldItalics()
 *         clickable(textview) { foo() }
 *         foregroundColor(getColor(R.color.light_urple))
 *     }
 *
 * @param[String] (the extended class) The complete string, some or all of which will have s
 *                pannables applied.
 * @param[substring] The part of the String you want to apply spannables to.
 * @param[enkompass] Any function of the [Enkompass] class. This lambda applies spannables to
 *                  the [substring].
 *
 * @return The resulting [SpannableStringBuilder], with the applied spannables.
 */
fun String.enkompass(substring: String, enkompass: Enkompass.() -> Unit)
        = Enkompass(this, Enkompass.SubstringStrategy(this, substring)).apply(enkompass)

fun String.enkompass(intRange: IntRange, enkompass: Enkompass.() -> Unit)
        = Enkompass(this, Enkompass.RangeStrategy(intRange)).apply(enkompass)


/**
 * This is not intended to be used directly.
 */
@SuppressLint("VisibleForTests")
@Deprecated("I made you extension functions!")
class Enkompass(string: String, private val strategy: Strategy) : SpannableString(string) {

    interface Strategy {
        val range: IntRange
    }

    class SubstringStrategy(string: String, substring: String) : Strategy {
        override val range = string.which(substring)
    }

    class RangeStrategy(override val range: IntRange) : Strategy


    fun bold() = apply { enkompass(strategy, StyleSpan(Typeface.BOLD)) }

    fun italics() = apply { enkompass(strategy, StyleSpan(Typeface.ITALIC)) }

    fun boldItalics() = apply { enkompass(strategy, StyleSpan(Typeface.BOLD_ITALIC)) }

    fun monospace() = apply { enkompass(strategy, TypefaceSpan("monospace")) }

    fun style(context: Context, @StyleRes style: Int)
            = apply { enkompass(strategy, TextAppearanceSpan(context, style)) }

    fun foregroundColor(resolvedColor: Int)
            = apply { enkompass(strategy, ForegroundColorSpan(resolvedColor)) }

    fun backgroundColor(resolvedColor: Int)
            = apply { enkompass(strategy, BackgroundColorSpan(resolvedColor)) }

    fun clickable(
            textview: TextView,
            movementMethod: LinkMovementMethod? = LinkMovementMethod(),
            click: (View) -> Unit
    ) = apply {
        textview.movementMethod = movementMethod
        enkompass(strategy, object : ClickableSpan() {
            override fun onClick(widget: View) = click(widget)
        })
    }

    fun image(drawable: Drawable, alignBaseline: Boolean) = apply {
        val alignment = if (alignBaseline) ImageSpan.ALIGN_BASELINE else ImageSpan.ALIGN_BOTTOM
        enkompass(strategy, ImageSpan(drawable, alignment))
    }

    fun custom(spans: () -> Any) = enkompass(strategy, spans())
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun CharSequence.enkompass(strategy: Enkompass.Strategy, vararg spans: Any): Spannable {
    val substring = substring(strategy.range.let { it.first until it.last })
    if (substring !in this) {
        throw IllegalArgumentException("Substring not contained in the given String.")
    }

    val spannable = this as? Spannable ?: SpannableString(this)
    spans.forEach {
        spannable.setSpan(it, strategy.range.first, strategy.range.last, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    }
    return spannable
}

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun CharSequence.which(substring: String): IntRange {
    val start = indexOf(substring)
    val end = start + substring.length
    return start..end
}
