package com.example.myapplication.model

data class signUpDataClass(
    val name : String ?= null,
    val email : String ?= null,
    val imgProfile : String ?= null,
    val signUpMethod : String ?= null
)
