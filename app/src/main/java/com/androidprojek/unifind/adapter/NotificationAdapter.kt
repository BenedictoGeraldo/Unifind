package com.androidprojek.unifind.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidprojek.unifind.R
import com.androidprojek.unifind.model.NotificationModel

// Ubah parameter menjadi var agar bisa diubah
class NotificationAdapter(private var notificationList: List<NotificationModel>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.iv_notification_icon)
        val message: TextView = itemView.findViewById(R.id.tv_notification_message)
        val timestamp: TextView = itemView.findViewById(R.id.tv_notification_timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.icon.setImageResource(notification.iconResId)
        holder.message.text = notification.message
        holder.timestamp.text = notification.timestamp
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    // <-- FUNGSI BARU UNTUK UPDATE DATA
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newNotificationList: List<NotificationModel>) {
        this.notificationList = newNotificationList
        notifyDataSetChanged() // Perintahkan RecyclerView untuk menggambar ulang dirinya
    }
}