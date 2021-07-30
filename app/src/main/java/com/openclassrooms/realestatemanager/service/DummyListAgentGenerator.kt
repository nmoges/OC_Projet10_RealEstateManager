package com.openclassrooms.realestatemanager.service

import com.openclassrooms.realestatemanager.model.Agent

class DummyListAgentGenerator {
    companion object {
        val listAgents: MutableList<Agent> = mutableListOf(
            Agent(1,"Chris", "Evans"),
            Agent(2,"Scarlett", "Johansson"),
            Agent(3,"Elon", "Musk"),
            Agent(4,"Clark", "Kent"),
            Agent(5,"John", "Locke"),
            Agent(6,"Marie", "Curie"),
            Agent(7,"Jackie", "Chan"),
        )
    }
}