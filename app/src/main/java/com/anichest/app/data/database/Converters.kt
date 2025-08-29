package com.anichest.app.data.database

import androidx.room.TypeConverter
import com.anichest.app.data.entity.Priority
import com.anichest.app.data.entity.WatchStatus

/**
 * Room用のTypeConverter
 * EnumをStringに変換してデータベースに保存
 */
class Converters {

    @TypeConverter
    fun fromWatchStatus(status: WatchStatus): String {
        return status.name
    }

    @TypeConverter
    fun toWatchStatus(statusString: String): WatchStatus {
        return WatchStatus.valueOf(statusString)
    }

    @TypeConverter
    fun fromPriority(priority: Priority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(priorityString: String): Priority {
        return Priority.valueOf(priorityString)
    }
}