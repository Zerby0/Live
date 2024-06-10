package com.example.live

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class AchievementAdapter(private var achievements: List<Achievement>) :
    RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.achievement_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.achievement_description)
        val iconImageView: ImageView = itemView.findViewById(R.id.achievement_icon)
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

        // Imposta l'icona in base al valore booleano
        if (achievement.isCompleted) {
            holder.iconImageView.setImageResource(R.drawable.ic_star_true)
            holder.iconImageView.contentDescription = holder.itemView.context.getString(R.string.achievement_completed)
        } else {
            holder.iconImageView.setImageResource(R.drawable.ic_star_false)
            holder.iconImageView.contentDescription = holder.itemView.context.getString(R.string.achievement_not_completed)
        }
    }

    fun setAchievements(newAchievements: List<Achievement>) {
        this.achievements = newAchievements
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int = achievements.size

}