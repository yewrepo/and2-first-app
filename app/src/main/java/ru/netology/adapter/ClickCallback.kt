package ru.netology.adapter

interface ClickCallback {
    fun onLikeClick(position: Int)
    fun onShareClick(position: Int)
}