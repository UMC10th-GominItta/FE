package com.gominitta.android.presentation.recipe

/**
 * 마음 레시피 목록에 표시할 UI용 데이터 모델.
 *
 * 지금은 단순 UI 구현 단계라 presentation 레이어에 둔다.
 * 나중에 서버/API/DB 저장이 붙으면 domain/model/Recipe.kt로 이동하는 것이 더 적절하다.
 */
data class RecipeItem(
    val id: Long,
    val title: String,
    val description: String,
    val durationMinutes: Int
)

/**
 * D102 레시피 실행 화면의 상태.
 *
 * Ready:
 * - 시작 전 상태
 * - 타이머 안에 5:00 표시
 * - 시작하기 버튼 표시
 *
 * Running:
 * - 실행 중 상태
 * - 타이머가 감소하는 상태
 * - 완료하기 버튼 표시
 *
 * Completed:
 * - 완료 상태
 * - 원형 타이머 안에 "완료" 표시
 * - 완료하기 버튼 표시
 */
enum class RecipeRunStatus {
    Ready,
    Running,
    Completed
}

/**
 * D104 새 레시피 등록 화면에서 사용하는 추천 레시피 데이터.
 *
 * 아직 기획 문구가 확정되지 않았으므로 임시값으로 작성한다.
 * 나중에 Figma/기획 확정 후 title, description, durationMinutes만 교체하면 된다.
 */
data class RecommendedRecipe(
    val title: String,
    val description: String,
    val durationMinutes: Int
)

/**
 * 마음 레시피 화면 전체에서 사용할 UI 상태.
 *
 * 이번 주 목표는 단순 UI 구현이므로:
 * - 서버 저장 없음
 * - 계정별 저장 없음
 * - 앱 재실행 후 데이터 유지 없음
 * - 더미 데이터 기반 화면 확인용
 */
data class RecipeUiState(
    val recipes: List<RecipeItem> = emptyList(),
    val selectedRecipeId: Long? = null,
    val runStatus: RecipeRunStatus = RecipeRunStatus.Ready,
    val remainingSeconds: Int = 0,
    val createTitle: String = "",
    val createDescription: String = "",
    val createDuration: String = ""
) {
    /**
     * 현재 선택된 레시피.
     * D101에서 카드를 클릭했을 때 D102 실행 화면에 전달할 데이터다.
     */
    val selectedRecipe: RecipeItem?
        get() = recipes.firstOrNull { it.id == selectedRecipeId }

    /**
     * D104 등록 버튼 활성화 여부.
     * 레시피명, 수행 방법, 예상 소요 시간이 모두 입력되었을 때 true.
     */
    val isCreateEnabled: Boolean
        get() = createTitle.isNotBlank() &&
                createDescription.isNotBlank() &&
                createDuration.isNotBlank()
}

/**
 * D101 마음 레시피 센터에 표시할 더미 레시피 목록.
 *
 * Figma 기준 화면 확인용 데이터다.
 * 실제 API 연결 시에는 Repository/ViewModel을 통해 받아오도록 변경한다.
 */
val sampleRecipes = listOf(
    RecipeItem(
        id = 1L,
        title = "룸 스프레이 뿌리기",
        description = "좋아하는 향의 룸 스프레이를 방 안 곳곳에 뿌리고, 눈을 감고 깊게 향을 들이마시며 5분간 휴식합니다.",
        durationMinutes = 5
    ),
    RecipeItem(
        id = 2L,
        title = "추억의 플레이 리스트 재생하기",
        description = "좋아하는 노래를 틀고 잠시 감정을 정리합니다.",
        durationMinutes = 15
    ),
    RecipeItem(
        id = 3L,
        title = "뜨거운 물로 오래 샤워하기",
        description = "따뜻한 물로 몸의 긴장을 풀어봅니다.",
        durationMinutes = 20
    ),
    RecipeItem(
        id = 4L,
        title = "노래 들으며 말랑이 만지기",
        description = "편안한 음악을 들으며 손의 감각에 집중합니다.",
        durationMinutes = 15
    )
)

/**
 * D104 새 레시피 등록 화면에서 사용할 임시 추천 레시피 목록.
 */
val sampleRecommendedRecipes = listOf(
    RecommendedRecipe(
        title = "심호흡 5번 하기",
        description = "눈을 감고 코로 깊게 들이마시고 입으로 천천히 내뱉는 심호흡을 5번 반복합니다.",
        durationMinutes = 1
    ),
    RecommendedRecipe(
        title = "찬물 한 컵 마시기",
        description = "차가운 물을 천천히 마시며 몸의 감각에 집중합니다.",
        durationMinutes = 2
    ),
    RecommendedRecipe(
        title = "스트레칭 하기",
        description = "목과 어깨를 천천히 돌리며 몸의 긴장을 풀어줍니다.",
        durationMinutes = 3
    )
)