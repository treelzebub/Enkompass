package net.treelzebub.example

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import net.treelzebub.enkompass.enkompass
import net.treelzebub.enkompass.example.MainActivity
import net.treelzebub.enkompass.example.R
import net.treelzebub.enkompass.which
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class Integration {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    val textview: TextView
        get() = activityRule.activity.findViewById<TextView>(R.id.textview_string)!!

    @Test fun clickable() {
        val str = "Test string"
        val sub = "string"

        var flag = false
        val spannable = str.enkompass(sub) {
            clickable(textview) { flag = true}
        }

        val clickable = spannable.findSpan<ClickableSpan>(sub)
        clickable.onClick(textview)
        assertTrue(flag)
    }

    @Test fun foregroundColor() {
        val str = "Gabba gabba hey"
        val sub = "hey"
        val black = ContextCompat.getColor(activityRule.activity, android.R.color.black)

        val spannable = str.enkompass(sub) {
            foregroundColor(black)
        }

        val span = spannable.findSpan<ForegroundColorSpan>(sub)
        assertEquals(black, span.foregroundColor)
    }

    @Test fun backgroundColor() {
        val str = "Gabba gabba hey"
        val sub = "hey"
        val black = ContextCompat.getColor(activityRule.activity, android.R.color.black)

        val spannable = str.enkompass(sub) {
            backgroundColor(black)
        }

        val span = spannable.findSpan<BackgroundColorSpan>(sub)
        assertEquals(black, span.backgroundColor)
    }

    private inline fun <reified Span : Any> Spannable.findSpan(substring: String): Span {
        val which = which(substring)
        val spans = getSpans(which.first, which.last, Span::class.java)
        return spans.find { it is Span }!!
    }
}

