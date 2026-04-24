package com.example.soundplay.core

import com.google.firebase.auth.FirebaseUser

interface Authentication {
    suspend fun requestLogin(email: String, password: String): FirebaseUser?
    suspend fun requestSignUp(email: String, password: String): FirebaseUser?
}