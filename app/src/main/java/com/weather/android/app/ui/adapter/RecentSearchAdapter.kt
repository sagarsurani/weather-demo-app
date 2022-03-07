package com.weather.android.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.weather.android.app.R
import com.weather.android.app.data.models.RecentSearchHistory
import com.weather.android.app.data.local.prefs.PreferenceDataHelper
import kotlinx.android.synthetic.main.item_search.view.*

class RecentSearchAdapter(
    recentSearchList: ArrayList<RecentSearchHistory>,
    private val onItemClickListener: (RecentSearchHistory) -> Unit,
    private val onDeleteItemClick:()->Unit
) :
    RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {
    private var searchList: ArrayList<RecentSearchHistory>? = recentSearchList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_search,
            parent,
            false
        )
        return RecentSearchViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecentSearchViewHolder, position: Int) {
        holder.itemView.ivDelete.setOnClickListener {
            searchList!!.remove(searchList!![position])
            PreferenceDataHelper.getInstance(holder.itemView.context)!!.deleteSearchItem(position)
            onDeleteItemClick()
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            onItemClickListener(searchList!![position])
        }
        holder.bind(searchList!![position])
    }

    override fun getItemCount(): Int {
        return searchList!!.size
    }

    fun setList(list: ArrayList<RecentSearchHistory>) {
        searchList = list
        notifyDataSetChanged()
    }

    class RecentSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: RecentSearchHistory) {
            itemView.tvSearchLocation.text = item.title
        }
    }
}