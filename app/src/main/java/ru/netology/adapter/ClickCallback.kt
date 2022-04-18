package ru.netology.adapter

interface ClickCallback {
    fun onOpenClick(position: Int)
    fun onLikeClick(position: Int)
    fun onShareClick(position: Int)
    fun onRemoveClick(position: Int)
    fun onEditClick(position: Int)
    fun onYoutubeLinkClick(position: Int)
    fun onPhotoOpenClick(position: Int)
    fun onAdOpenClick(position: Int)
}