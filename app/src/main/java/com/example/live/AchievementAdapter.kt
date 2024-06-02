package com.example.live

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Achievement (val title: String, val description: String)
class AchievementAdapter(private val achievements: List<Achievement>) :
    RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.achievement_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.achievement_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.achievement_item, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.titleTextView.text = achievement.title
        holder.descriptionTextView.text = achievement.description
    }

    override fun getItemCount(): Int = achievements.size
}
