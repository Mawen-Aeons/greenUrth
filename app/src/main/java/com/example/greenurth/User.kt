package com.example.greenurth

import com.google.firebase.database.Exclude

class User {

    var userName:String? = null
    var userEmail:String? = null
    var userAddress:String? = null
    var userContactNo:String? = null
    var userProfilePicture:String? = null


    constructor(){}

    constructor(
        userName: String?,
        userEmail: String?,
        userContactNo: String?,
        userAddress: String?,
        userProfilePicture: String?
    ) {
        this.userName = userName
        this.userEmail = userEmail
        this.userContactNo = userContactNo
        this.userAddress = userAddress
        this.userProfilePicture = userProfilePicture
    }

    constructor(userName: String?, userEmail: String?, userProfilePicture: String?) {
        this.userName = userName
        this.userEmail = userEmail
        this.userProfilePicture = userProfilePicture
    }

    @Exclude
    fun toMap():Map<String,Any>{
        val result = HashMap<String, Any>()
        result.put("userName",userName!!)
        result.put("userEmail",userEmail!!)
        result.put("userContactNo",userContactNo!!)
        result.put("userAddress",userAddress!!)
        result.put("userProfilePicture",userProfilePicture!!)

        return result
    }
}