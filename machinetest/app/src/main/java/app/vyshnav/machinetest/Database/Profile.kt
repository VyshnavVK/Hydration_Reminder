package app.vyshnav.machinetest.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    var name: String,

    var email: String,

    var dob : String,

    var gender : String,

    var password : String,

    var imageLocation :String,

    var isLoggedIn : Int?
)
