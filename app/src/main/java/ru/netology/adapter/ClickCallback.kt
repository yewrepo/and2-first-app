package ru.netology.adapter

interface ClickCallback {
    fun onLikeClick(position: Int)
    fun onShareClick(position: Int)
    fun onRemoveClick(position: Int)
    fun onEditClick(position: Int)
}