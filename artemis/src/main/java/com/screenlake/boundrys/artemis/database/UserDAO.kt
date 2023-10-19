package com.screenlake.boundrys.artemis.database

import androidx.room.*

@Dao
interface UserDAO {
    //database interaction, uses coroutines
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserObj(user: User)

    @Query("SELECT EXISTS (SELECT 1 FROM user_table)")
    suspend fun userExists(): Boolean

    @Query("SELECT * FROM user_table LIMIT 1")
    suspend fun getUser(): User

    @Query("SELECT * FROM user_table LIMIT 1")
    fun getUserSynchronously(): User

    @Query("UPDATE user_table SET panel_id = :panelId WHERE id = :id")
    fun updatePanel(id:Int, panelId: String)

    /**
     * Deleting user
     */
    @Query("DELETE FROM user_table")
    suspend fun deleteUser()
}