package com.example.safespace_admin.entity


class User(val userId: String, val name: String, val email: String, val status: String){
    constructor():this("","","","")
}