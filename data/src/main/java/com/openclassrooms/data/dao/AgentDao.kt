package com.openclassrooms.data.dao

import androidx.room.*
import com.openclassrooms.data.entities.AgentData

@Dao
interface AgentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAgentData(agentData: AgentData): Long

    @Update
    suspend fun updateAgentData(agentData: AgentData)

    @Query("SELECT * FROM table_agents WHERE id = :id")
    suspend fun getAgentById(id: Long): AgentData

    @Query("SELECT * FROM table_agents")
    suspend fun getAllAgents(): List<AgentData>
}