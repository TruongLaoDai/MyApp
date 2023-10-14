package com.smile.watchmovie.model

import com.google.firebase.firestore.Exclude

data class UserInfo(
    var id: String = "",
    var fullName: String = "",
    var address: String = "",
    var phone: String = "",
    var gender: String = "",
    var isVip: String = "0",
    @Exclude
    var documentId: String = ""
)
