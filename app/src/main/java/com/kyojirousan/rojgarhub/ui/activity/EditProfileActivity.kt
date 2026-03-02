package com.kyojirousan.rojgarhub.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.kyojirousan.rojgarhub.databinding.ActivityEditProfileBinding
import com.kyojirousan.rojgarhub.model.UserModel
import com.kyojirousan.rojgarhub.repository.UserRepositoryImpl
import com.kyojirousan.rojgarhub.utils.LoadingUtils
import com.kyojirousan.rojgarhub.viewModel.UserViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var loadingUtils: LoadingUtils
    private var currentUser: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRepository = UserRepositoryImpl()
        userViewModel = UserViewModel(userRepository)
        loadingUtils = LoadingUtils(this)

        currentUser = intent.getParcelableExtra("USER_DATA")

        if (currentUser == null) {
            Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        populateFields()

        binding.btnSave.setOnClickListener {
            saveChanges()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun populateFields() {
        currentUser?.let { user ->
            binding.editUsername.setText(user.firstName)
            binding.editAddress.setText(user.address)
            binding.editPhone.setText(user.phoneNumber)

            if (user.role == "employer") {
                binding.radioEmployer.isChecked = true
            } else {
                binding.radioJobSeeker.isChecked = true
            }
        }
    }

    private fun saveChanges() {
        val username = binding.editUsername.text.toString().trim()
        val address = binding.editAddress.text.toString().trim()
        val phone = binding.editPhone.text.toString().trim()
        val role = if (binding.radioJobSeeker.isChecked) "jobseeker" else "employer"

        // Validate input
        if (username.isEmpty()) {
            binding.editUsername.error = "Username is required"
            return
        }

        if (phone.isEmpty()) {
            binding.editPhone.error = "Phone number is required"
            return
        }

        // Create map with changes
        val updates = mutableMapOf<String, Any>()
        updates["firstName"] = username
        updates["address"] = address
        updates["phoneNumber"] = phone
        updates["role"] = role

        loadingUtils.show()
        currentUser?.userId?.let { userId ->
            // Note: The editProfile functionality needs to be added to UserRepository and implemented
            // For now, updating the fields in the user object
            Toast.makeText(this, "Save function called", Toast.LENGTH_SHORT).show()
            loadingUtils.dismiss()
            finish()
        }
    }
}
