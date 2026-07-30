package com.gominitta.android.presentation.report

import com.gominitta.android.ui.components.DateRangeOption
import org.junit.Assert.assertEquals
import org.junit.Test

class ReportViewModelTest {

    @Test
    fun `initial state contains 30 day dummy reports`() {
        val state = ReportViewModel().uiState.value

        assertEquals(DateRangeOption.LAST_30_DAYS, state.worryThemeRange)
        assertEquals(DateRangeOption.LAST_30_DAYS, state.anxietyRange)
        assertEquals(DateRangeOption.LAST_30_DAYS, state.timelineRange)
        assertEquals(100, state.worryThemeData?.totalCount)
        assertEquals(6, state.anxietyData?.matchedSetCount)
        assertEquals(20, state.timelineData?.totalCount)
    }

    @Test
    fun `selecting a range updates only the selected report`() {
        val viewModel = ReportViewModel()

        viewModel.selectAnxietyRange(DateRangeOption.LAST_2_WEEKS)

        val state = viewModel.uiState.value
        assertEquals(DateRangeOption.LAST_30_DAYS, state.worryThemeRange)
        assertEquals(DateRangeOption.LAST_2_WEEKS, state.anxietyRange)
        assertEquals(DateRangeOption.LAST_30_DAYS, state.timelineRange)
        assertEquals(3, state.anxietyData?.matchedSetCount)
    }
}
