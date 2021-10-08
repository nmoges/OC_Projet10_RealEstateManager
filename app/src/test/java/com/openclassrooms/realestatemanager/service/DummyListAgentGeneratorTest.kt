package com.openclassrooms.realestatemanager.service

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DummyListAgentGeneratorTest {

    @Test
    fun test_list_agents() {
        assertEquals(true, DummyListAgentGenerator.listAgents.isNotEmpty())
    }
}