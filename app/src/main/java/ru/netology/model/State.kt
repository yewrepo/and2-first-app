package ru.netology.nmedia

object Loading : State()
object Error : State()
object Success : State()

sealed class State
