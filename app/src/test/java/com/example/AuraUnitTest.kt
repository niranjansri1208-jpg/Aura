package com.example

import com.example.data.model.AgentType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuraUnitTest {
    @Test
    fun testAllEightAgentsAreConfigured() {
        val agents = AgentType.values()
        assertEquals(8, agents.size)
        agents.forEach { agent ->
            assertTrue(agent.displayName.isNotBlank())
            assertTrue(agent.subtitle.isNotBlank())
            assertTrue(agent.systemPrompt.isNotBlank())
            assertNotNull(agent.getAccentColor())
        }
    }
}
