package com.bagel.noink.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "记录下今天的故事吧~"
    }
    val text: LiveData<String> = _text

    private val _count_of_card = MutableLiveData<Int>().apply {
        value = 1
    }
    val count_of_card: LiveData<Int> = _count_of_card

    fun updateCount(newValue: Int) {
        _count_of_card.value = newValue
    }
}