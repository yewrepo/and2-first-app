package ru.netology.model

object Loading : State()
object Error : State()
object Success : State()

sealed class State
