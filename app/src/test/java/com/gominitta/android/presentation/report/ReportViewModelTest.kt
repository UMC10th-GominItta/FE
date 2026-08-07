package com.gominitta.android.presentation.report

import com.gominitta.android.data.remote.ApiResult
import com.gominitta.android.domain.model.report.WorryThemeCount
import com.gominitta.android.domain.model.report.WorryThemeReport
import com.gominitta.android.domain.model.report.AnxietyGapReport
import com.gominitta.android.domain.model.report.ReportDayOfWeek
import com.gominitta.android.domain.model.report.ReportTimeSlot
import com.gominitta.android.domain.model.report.WorryTimelineCell
import com.gominitta.android.domain.model.report.WorryTimelinePeak
import com.gominitta.android.domain.model.report.WorryTimelineReport
import com.gominitta.android.domain.repository.ReportRepository
import com.gominitta.android.ui.components.DateRangeOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads 30 day worry theme report`() = runTest(dispatcher) {
        val viewModel = ReportViewModel(FakeReportRepository())
        advanceUntilIdle()
        val state = viewModel.uiState.value

        assertEquals(DateRangeOption.LAST_30_DAYS, state.worryThemeRange)
        assertEquals(DateRangeOption.LAST_30_DAYS, state.anxietyRange)
        assertEquals(DateRangeOption.LAST_30_DAYS, state.timelineRange)
        assertEquals(5L, state.worryThemeData?.totalCount)
        assertEquals("30d", state.worryThemeData?.period)
        assertEquals(WorryTheme.PRESENTATION, state.worryThemeData?.themes?.last()?.theme)
        assertEquals(12L, state.anxietyData?.sampleCount)
        assertEquals(-4.0, state.anxietyData?.gap)
        assertEquals(20L, state.timelineData?.totalCount)
        assertEquals(1, state.timelineData?.levels?.get(0)?.get(0))
        assertEquals(4, state.timelineData?.levels?.get(2)?.get(3))
        assertEquals(4, state.timelineData?.levels?.get(3)?.get(6))
    }

    @Test
    fun `selecting a range updates only the selected report`() = runTest(dispatcher) {
        val viewModel = ReportViewModel(FakeReportRepository())
        advanceUntilIdle()

        viewModel.selectAnxietyRange(DateRangeOption.LAST_2_WEEKS)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(DateRangeOption.LAST_30_DAYS, state.worryThemeRange)
        assertEquals(DateRangeOption.LAST_2_WEEKS, state.anxietyRange)
        assertEquals(DateRangeOption.LAST_30_DAYS, state.timelineRange)
        assertEquals("2w", state.anxietyData?.period)
    }

    @Test
    fun `각 카드의 로딩 상태를 독립적으로 관리한다`() = runTest(dispatcher) {
        val viewModel = ReportViewModel(SlowWorryThemeRepository())

        runCurrent()

        assertTrue(viewModel.uiState.value.isWorryThemeLoading)
        assertFalse(viewModel.uiState.value.isAnxietyLoading)

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isWorryThemeLoading)
    }

    private class FakeReportRepository : ReportRepository {
        override suspend fun getWorryThemes(period: String): ApiResult<WorryThemeReport> =
            ApiResult.Success(
                WorryThemeReport(
                    period = period,
                    topCategory = "진로",
                    themes = listOf(
                        WorryThemeCount(category = "진로", count = 1),
                        WorryThemeCount(category = "발표", count = 4),
                    ),
                    feedback = "최근에는 진로와 관련된 걱정이 가장 많았어요.",
                ),
            )

        override suspend fun getAnxietyGap(period: String): ApiResult<AnxietyGapReport> =
            ApiResult.Success(
                AnxietyGapReport(
                    period = period,
                    beforeScore = 8,
                    afterScore = 4,
                    gap = -4,
                    sampleCount = 12,
                    feedback = "걱정을 마주하고 마음이 한결 가벼워졌어요.",
                ),
            )

        override suspend fun getWorryTimeline(period: String): ApiResult<WorryTimelineReport> =
            ApiResult.Success(worryTimelineReport(period))
    }

    private class SlowWorryThemeRepository : ReportRepository {
        override suspend fun getWorryThemes(period: String): ApiResult<WorryThemeReport> {
            delay(1_000)
            return ApiResult.Success(worryThemeReport(period))
        }

        override suspend fun getAnxietyGap(period: String): ApiResult<AnxietyGapReport> =
            ApiResult.Success(anxietyGapReport(period))

        override suspend fun getWorryTimeline(period: String): ApiResult<WorryTimelineReport> =
            ApiResult.Success(worryTimelineReport(period))
    }

}

private fun worryThemeReport(period: String) = WorryThemeReport(
    period = period,
    topCategory = "진로",
    themes = listOf(WorryThemeCount(category = "진로", count = 1)),
    feedback = "최근에는 진로와 관련된 걱정이 가장 많았어요.",
)

private fun anxietyGapReport(period: String) = AnxietyGapReport(
    period = period,
    beforeScore = 8,
    afterScore = 4,
    gap = -4,
    sampleCount = 12,
    feedback = "걱정을 마주하고 마음이 한결 가벼워졌어요.",
)

private fun worryTimelineReport(period: String) = WorryTimelineReport(
    period = period,
    cells = listOf(
        WorryTimelineCell(ReportDayOfWeek.MON, ReportTimeSlot.MORNING, 1),
        WorryTimelineCell(ReportDayOfWeek.THU, ReportTimeSlot.EVENING, 6),
        WorryTimelineCell(ReportDayOfWeek.SUN, ReportTimeSlot.DAWN, 13),
    ),
    peaks = listOf(
        WorryTimelinePeak(ReportDayOfWeek.THU, ReportTimeSlot.EVENING),
        WorryTimelinePeak(ReportDayOfWeek.SUN, ReportTimeSlot.DAWN),
    ),
    feedback = "목요일 저녁 시간대와 일요일 밤 시간대에 걱정 기록이 많았어요.",
)
