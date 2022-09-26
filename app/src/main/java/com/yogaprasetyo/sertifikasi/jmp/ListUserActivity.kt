package com.yogaprasetyo.sertifikasi.jmp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogaprasetyo.sertifikasi.jmp.databinding.ActivityListUserBinding
import com.yogaprasetyo.sertifikasi.jmp.db.UserHelper

/**
 * created by yogaprasetyo on 22/09/26
 *
 * Handle load all data from DB. Showing the data using Recycler View with [UserAdapter]
 * In this activity also can add new data using click floating action button
 * */
class ListUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListUserBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var db: UserHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userAdapter = UserAdapter(clickItemCallbackImpl)
        db = UserHelper.getInstance(this)

        initRecyclerView()
        initListenerButton()
        loadAllUserFromDB()
    }

    /**
     * Implement interface callback item clicked on adapter
     * */
    private val clickItemCallbackImpl = object : UserAdapter.OnItemClickCallback {
        override fun onItemClicked(selectedUser: UserModel) {
            val intent = Intent(this@ListUserActivity, DetailActivity::class.java)
                .putExtra(DetailActivity.EXTRA_USER, selectedUser)
            startActivity(intent)
        }
    }

    /**
     * Setup the configurations Recycler View that needed as layoutManager and adapter
     * */
    private fun initRecyclerView() {
        binding.rvUser.apply {
            layoutManager = LinearLayoutManager(this@ListUserActivity)
            adapter = userAdapter
        }
    }

    /**
     * Load all user from DB and add to adapter for showing the data
     * */
    private fun loadAllUserFromDB() {
        db.open()
        val data = db.queryAll()
        userAdapter.userList = data
        db.close()
    }

    /**
     * Setup click listener for button (floatingActionButton)
     * */
    private fun initListenerButton() {
        binding.fabAdd.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
    }
}