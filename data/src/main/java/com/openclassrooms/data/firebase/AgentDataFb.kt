package com.openclassrooms.data.firebase

/**
 * Defines a node in a JSON tree stored in a Realtime database.
 * @param firstName : first name of an agent
 * @param lastName : last name of an agent
 */
data class AgentDataFb(
    var firstName: String =  "",
    var lastName: String = ""
)