package com.example.live

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView



class AchievementAdapter(private var achievements: List<Achievement>) :
    RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder>() {

    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.achievement_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.achievement_description)
        val iconImageView: ImageView = itemView.findViewById(R.id.achievement_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.achievement_item, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = achievements[position]
        holder.titleTextView.text = achievement.title
        holder.descriptionTextView.text = achievement.description
        holder.iconImageView.setImageResource(
            if (achievement.isCompleted) R.drawable.ic_star_true else R.drawable.ic_star_false
        )
    }

    fun setAchievements(newAchievements: List<Achievement>) {
        val diffResult = DiffUtil.calculateDiff(AchievementDiffCallback(achievements, newAchievements))
        achievements = newAchievements
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = achievements.size

    class AchievementDiffCallback(
        private val oldList: List<Achievement>,
        private val newList: List<Achievement>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].title == newList[newItemPosition].title
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}