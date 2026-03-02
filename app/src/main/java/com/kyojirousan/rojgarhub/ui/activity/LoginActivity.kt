package com.kyojirousan.rojgarhub.ui.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kyojirousan.rojgarhub.R
import com.kyojirousan.rojgarhub.databinding.ActivityLoginBinding
import com.kyojirousan.rojgarhub.repository.UserRepositoryImpl
import com.kyojirousan.rojgarhub.utils.LoadingUtils
import com.kyojirousan.rojgarhub.viewModel.UserViewModel

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var userViewModel: UserViewModel
    lateinit var loadingUtils: LoadingUtils
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = UserViewModel(UserRepositoryImpl())
        loadingUtils = LoadingUtils(this)

        sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)

        binding.btnLogin.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loadingUtils.show()
            userViewModel.login(email, password) { success, message ->
                loadingUtils.dismiss()
                if (success) {
                    val editor = sharedPreferences.edit()
                    editor.putString("email", email)
                    editor.apply()

                    val intent = Intent(this, NavigationActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.signupTab.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
