package com.ptilou.app.core.miseajour

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MiseAJourTest {
    @Test
    fun comparaisonDesVersions() {
        assertTrue(estPlusRecente("1.3", "1.2"))
        assertTrue(estPlusRecente("1.10", "1.9"))
        assertTrue(estPlusRecente("2.0", "1.9.9"))
        assertFalse(estPlusRecente("1.2", "1.2"))
        assertFalse(estPlusRecente("1.2", "1.2.0"))
        assertFalse(estPlusRecente("1.1", "1.2"))
    }
}
