package org.freedu.realtimeb5

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.freedu.realtimeb5.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // 🔗 ViewBinding & Firebase references
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    // 📝 User data list and editing state
    private var userList = mutableListOf<User>()
    private var editUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Setup view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Initialize Firebase Auth and Realtime Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Users")

        // 🔘 Set listeners
        setListeners()

        // 📥 Read and display data
        readUserData()
    }

    /**
     * 🎯 Set up all UI click listeners (logout, save)
     */
    private fun setListeners() {
        // Logout button with confirmation
        binding.logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // Save or Update button
        binding.saveButton.setOnClickListener {
            saveOrUpdateUser()
        }
    }

    /**
     * 🧠 Handles save and update logic for user data
     */
    private fun saveOrUpdateUser() {
        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()

        if (name.isBlank() || email.isBlank()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (editUserId == null) {
            // ➕ Create new user
            val userId = database.push().key!!
            val user = User(userId, name, email)
            database.child(userId).setValue(user)
            Toast.makeText(this, "Data added", Toast.LENGTH_SHORT).show()
        } else {
            // ✏️ Update existing user
            val updatedUser = User(editUserId, name, email)
            database.child(editUserId!!).setValue(updatedUser)
            Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show()
            editUserId = null
            binding.saveButton.text = "Save"
        }

        // 🔄 Clear input fields and hide keyboard
        binding.nameInput.text.clear()
        binding.emailInput.text.clear()
        hideKeyboard()
    }

    /**
     * 🔁 Reads user data from Firebase and populates RecyclerView
     */
    private fun readUserData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                setupRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * 🧩 Sets up RecyclerView with adapter and user list
     */
    private fun setupRecyclerView() {
        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerView.adapter = UserAdapter(
            userList,
            onEditClick = { user ->
                // 🖊️ Load user data into input fields for editing
                binding.nameInput.setText(user.name)
                binding.emailInput.setText(user.email)
                editUserId = user.id
                binding.saveButton.text = "Update"
            },
            onDeleteClick = { user ->
                // 🗑️ Delete user from database
                database.child(user.id!!).removeValue()
                binding.nameInput.text.clear()
                binding.emailInput.text.clear()
                Toast.makeText(this, "Data deleted", Toast.LENGTH_SHORT).show()
            }
        )
    }

    /**
     * 🔐 Shows a confirmation dialog before logout
     */
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                val intent = Intent(this, SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * 📱 Hide keyboard manually
     */
    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * 📴 Hide keyboard when user taps outside input fields
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
