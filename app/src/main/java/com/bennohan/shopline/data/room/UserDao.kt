package com.bennohan.shopline.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.bennohan.shopline.data.User
import com.crocodic.core.data.CoreDao

@Dao
interface UserDao : CoreDao<User> {

    @Query("DELETE FROM User")
    suspend fun deleteAll()


    @Query("SELECT * FROM User WHERE idRoom = 1 ")
    fun getUser() : LiveData<User>

    @Query("SELECT EXISTS (SELECT 1 FROM User WHERE idRoom = 1)")
    suspend fun isLogin(): Boolean


}