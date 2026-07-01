package com.gominitta.android.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gominitta.android.data.repository.SampleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [HomeScreen].
 *
 * Injected by Hilt via [@HiltViewModel]. Depends on [SampleRepository]
 * through the interface, NOT on any concrete implementation — swapping
 * [FakeSampleRepository] for a real implementation requires zero changes here.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SampleRepository,
) : ViewModel() {

    private val _greeting = MutableStateFlow("")
    val greeting: StateFlow<String> = _greeting.asStateFlow()

    init {
        loadGreeting()
    }

    private fun loadGreeting() {
        viewModelScope.launch {
            _greeting.value = repository.getGreeting()
        }
    }
}
