package org.freedu.realtimeb5

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.freedu.realtimeb5.databinding.ItemUserBinding


class UserAdapter(
    private val userList: List<User>,
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    // ViewHolder class with binding
    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // Use the ViewBinding class to inflate the layout
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        // Use binding to access views
        holder.binding.userName.text = user.name
        holder.binding.userEmail.text = user.email
        holder.binding.btnEdit.setOnClickListener {
            onEditClick(user)
        }
        holder.binding.btnDelete.setOnClickListener {
            onDeleteClick(user)
        }
    }


    override fun getItemCount(): Int = userList.size
}

