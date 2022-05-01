package ru.netology.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class PostLoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingViewHolder>() {
    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        return LoadingViewHolder.create(parent, retry)
    }
}