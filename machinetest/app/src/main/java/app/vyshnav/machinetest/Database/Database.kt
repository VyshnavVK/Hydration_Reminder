package app.vyshnav.machinetest.Database

import androidx.room.Database
import androidx.room.RoomDatabase


/**
 * Main database call 'db' to access it
 **/
typealias db = app.vyshnav.machinetest.Database.Database
@Database(entities = [Profile::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun ProfileDAO(): ProfileDAO
}