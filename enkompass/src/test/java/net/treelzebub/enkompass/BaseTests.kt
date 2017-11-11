package net.treelzebub.enkompass

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by treelzebub on 11/11/2017
 */
class BaseTests {

    @Test fun test_substring_at_beginning() {
        val outer = "i\'m at the beginning"
        val inner = "i\'m"
        val range = 0..3
        assertEquals(range, outer.which(inner))
    }

    @Test fun test_substring_at_end() {
        assert(true)
    }

    @Test fun test_substring_in_middle() {
        val outer = "What Is The Deal"
        val inner = "The"
        val range = 8..11
        assertEquals(range, outer.which(inner))
    }
}