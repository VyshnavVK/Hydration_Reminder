package app.vyshnav.machinetest.Database

import androidx.room.*

@Dao
interface ProfileDAO {

    @Insert
    suspend fun insertProfile(profile: Profile)

    @Query("SELECT * FROM profile where email = :emailText AND password = :passwordText ")
    fun login(emailText :String , passwordText :String): List<Profile>

    @Delete
    suspend fun deleteProfile(profile: Profile)

    @Query("SELECT * FROM profile")
    fun getProfile(): Profile?

    @Query("SELECT isLoggedIn FROM profile where isLoggedIn = 1")
    fun isLoggedIn() : Int?

    @Query("UPDATE profile SET isLoggedIn = 1")
    fun setLogin()


    @Query("UPDATE profile SET imageLocation = :path, name = :name")
    fun editProfile(path : String,name:String)


}
