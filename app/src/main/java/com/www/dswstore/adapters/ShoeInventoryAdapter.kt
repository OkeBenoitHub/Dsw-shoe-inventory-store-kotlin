package com.www.dswstore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.www.dswstore.R
import com.www.dswstore.database.Shoe
import com.www.dswstore.databinding.ListShoeItemBinding

class ShoeInventoryAdapter(private val clickListener: ShoeItemClickListener, private val onShoeMarkAsFavoriteListener: OnShoeMarkAsFavoriteListener):
    ListAdapter<Shoe, ShoeInventoryAdapter.ViewHolder>(ShoeItemDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener,item, onShoeMarkAsFavoriteListener)
    }

    /**
     * Interface listener for button click shoe set as favorite
     */
    interface OnShoeMarkAsFavoriteListener {
        fun onShoeMarkAsFavorite(shoeId: Long, isFavored: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(private val binding: ListShoeItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: ShoeItemClickListener, item: Shoe, onShoeMarkAsFavoriteListener: OnShoeMarkAsFavoriteListener) {
            binding.shoe = item
            binding.shoeItemClickListener = clickListener

            // Check if a specific shoe has been marked as favorite
            if (item.is_favored) {
                binding.iconShoeFavored.setImageResource(R.drawable.ic_action_favored_fill)
            } else {
                binding.iconShoeFavored.setImageResource(R.drawable.ic_action_favored)
            }
            // mark shoe as favored tapped
            binding.markShoeAsFavorite.setOnClickListener {
                onShoeMarkAsFavoriteListener.onShoeMarkAsFavorite(item.id, item.is_favored)
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListShoeItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class ShoeItemDiffCallback : DiffUtil.ItemCallback<Shoe>() {
    override fun areItemsTheSame(oldItem: Shoe, newItem: Shoe): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Shoe, newItem: Shoe): Boolean {
        return oldItem == newItem
    }
}

/**
 * Shoe Item Click listener for recycler view
 */
class ShoeItemClickListener(val clickListener: (shoeId: Long) -> Unit) {
    fun onClick(shoe: Shoe) = clickListener(shoe.id)
}