package com.gominitta.android.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.domain.usecase.GetGreetingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [HomeScreen].
 *
 * Depends on the [GetGreetingUseCase] domain use case, NOT on a repository
 * or any data implementation — the presentation layer only knows the domain.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getGreeting: GetGreetingUseCase,
) : ViewModel() {

    private val _greeting = MutableStateFlow("")
    val greeting: StateFlow<String> = _greeting.asStateFlow()

    init {
        loadGreeting()
    }

    private fun loadGreeting() {
        viewModelScope.launch {
            _greeting.value = getGreeting().message
        }
    }
}
