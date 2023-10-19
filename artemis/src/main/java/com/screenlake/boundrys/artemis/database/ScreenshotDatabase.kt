package com.screenlake.boundrys.artemis.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.scroll.ScrollEventSegment
import com.screenlake.boundrys.artemis.behavior.behaviors.models.behavior.topic.TopicSeenInterval
import com.screenlake.boundrys.artemis.behavior.behaviors.models.doa.ScrollEventlDAO
import com.screenlake.boundrys.artemis.behavior.behaviors.models.doa.TopicSeenDAO

@Database(
    entities = [ScreenshotZip::class, AccessibilityEvent::class, AccessibilityEventFts::class, ScrollEventSegment::class, TopicSeenInterval::class],
    version = 44
)
abstract class ScreenshotDatabase: RoomDatabase() {

    abstract val  getScreenshotZipDao: ScreenshotZipDAO
    abstract val  getUserDao: UserDAO
    abstract val  getLogEventDAO: LogEventDAO
    abstract val  getAccessibilityEventDAO: AccessibilityEventDAO
    abstract val  getScrollEventlDAO: ScrollEventlDAO
    abstract val  getTopicSeenDAO: TopicSeenDAO

    companion object {

        @Volatile
        private var INSTANCE: ScreenshotDatabase? = null

        fun getInstance(context: Context): ScreenshotDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ScreenshotDatabase::class.java,
                        "TheReservoir",
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}