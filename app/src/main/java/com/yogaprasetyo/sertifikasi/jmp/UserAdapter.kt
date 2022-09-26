package com.yogaprasetyo.sertifikasi.jmp

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yogaprasetyo.sertifikasi.jmp.databinding.ItemUserBinding
import java.io.File

/**
 * created by yogaprasetyo on 22/09/26
 *
 * Handle RecyclerView for list of user data registration
 * */
class UserAdapter(private val onItemClickCallback: OnItemClickCallback) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // Add data from outside
    var userList = ArrayList<UserModel>()
        set(newData) {
            if (newData.size > 0) {
                this.userList.clear()
            }
            this.userList.addAll(newData)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(this.userList[position])
    }

    override fun getItemCount(): Int = this.userList.size

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userModel: UserModel) {
            val (id, name, _, phone, gender, _, imagePath) = userModel
            binding.apply {
                tvId.text = "ID: $id"
                tvName.text = "Nama: $name"
                tvPhone.text = "No. HP: $phone"
                tvGender.text = "Jenis kelamin: $gender"
                binding.ivProfile.setImageURI(Uri.fromFile(File(imagePath)))
            }

            // Clicked one item then move to Detail Activity
            itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(userModel)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(selectedUser: UserModel)
    }
}