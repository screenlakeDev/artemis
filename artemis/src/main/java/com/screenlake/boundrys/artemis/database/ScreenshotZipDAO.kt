package com.screenlake.boundrys.artemis.database

import androidx.room.*

@Dao
interface ScreenshotZipDAO {

    //database interaction, uses coroutines
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertZipObj(zipObj: ScreenshotZip)

    @Delete
    suspend fun deleteZipObj(zipObj: ScreenshotZip)

    @Delete
    fun deleteZipObjSync(zipObj: ScreenshotZip)

    @Query("SELECT * FROM screenshot_zip_table")
    suspend fun getAllZipObjs(): MutableList<ScreenshotZip>

    @Query("SELECT * FROM screenshot_zip_table")
    fun getAllZipObjsSynchronously(): MutableList<ScreenshotZip>

    @Query("UPDATE screenshot_zip_table SET toDelete=:toDelete WHERE id = :id")
    suspend fun flagZipForDeletion(id: Int, toDelete: Boolean)

    @Query("DELETE FROM screenshot_zip_table WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("DELETE FROM screenshot_zip_table WHERE id = :id")
    fun deleteSynchronous(id: Int)

    @Query("SELECT * FROM screenshot_zip_table")
    suspend fun getZipsToDelete(): List<ScreenshotZip>

    @Query("SELECT Count(*) FROM screenshot_zip_table")
    suspend fun getZipCount(): Int

    @Query("SELECT * FROM screenshot_zip_table where toDelete is 0 limit 1")
    suspend fun getZipToUpload(): List<ScreenshotZip>

    @Query("SELECT * FROM screenshot_zip_table where file is :fileName")
    suspend fun getScreenshotZipByFileName(fileName: String): ScreenshotZip

    @Query("SELECT * FROM screenshot_zip_table where toDelete is 1 limit 1")
    suspend fun getZipToDeleteFlagged(): List<ScreenshotZip>

    /**
     * Updating only price
     * By order id
     */
//    @Query("UPDATE screenshot_zip_table SET to_delete=:toDelete WHERE id = :id")
//    suspend fun update(toDelete: Boolean, id: Int)

    @Query("DELETE FROM screenshot_zip_table")
    suspend fun nukeTable()
    //
    @Query("SELECT COUNT(id) FROM screenshot_zip_table")
    suspend fun getCount(): Int
}