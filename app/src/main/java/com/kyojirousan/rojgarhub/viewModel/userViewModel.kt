package com.kyojirousan.rojgarhub.viewModel

import com.kyojirousan.rojgarhub.model.UserModel
import com.kyojirousan.rojgarhub.repository.UserRepository
import com.google.firebase.auth.FirebaseUser
import androidx.lifecycle.ViewModel

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {
        repository.login(email, password, callback)
    }

    fun signup(email: String, password: String, callback: (Boolean, String, String?) -> Unit) {
        repository.signup(email, password, callback)
    }

    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {
        repository.forgetPassword(email, callback)
    }

    fun addUserToDatabase(userId: String, userModel: UserModel, callback: (Boolean, String) -> Unit) {
        repository.addUserToDatabase(userId, userModel, callback)
    }

    fun getUserFromDatabase(userId: String, callback: (UserModel?, Boolean, String) -> Unit) {
        repository.getUserFromDatabase(userId, callback)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repository.getCurrentUser()
    }

    fun logout(callback: (Boolean, String) -> Unit) {
        repository.logout(callback)
    }
}
