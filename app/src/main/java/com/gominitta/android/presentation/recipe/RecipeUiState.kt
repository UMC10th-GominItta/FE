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
 * - 그만두기 / 완료하기 버튼 표시
 *
 * Paused:
 * - 일시정지 상태
 * - 타이머 감소 멈춤, remainingSeconds 유지
 * - 그만두기 / 완료하기 버튼 표시
 *
 * Completed:
 * - 완료 상태
 * - 원형 타이머 안에 "완료" 표시
 * - 완료하기 버튼만 단독 표시
 */
enum class RecipeRunStatus {
    Ready,
    Running,
    Paused,
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
 * D102-2 레시피 완료 화면에서 사용하는 완료 통계.
 * 서버 API(GET /api/v1/recipe-logs/summary) 응답 필드명과 동일하게 맞춰둠.
 */
data class RecipeCompletionSummary( // 추가
    val todayCompletedCount: Int = 0,
    val totalCompletedCount: Int = 0,
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
    val createDuration: String = "",
    val completionSummary: RecipeCompletionSummary = RecipeCompletionSummary() // 추가
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
        durationMinutes = 2
    ),
    RecommendedRecipe(
        title = "스트레칭 하기",
        description = "자리에 편안하게 서거나 앉아 양손을 깍지 끼고 하늘 위로 기지개를 쭉 켭니다. 숨을 천천히 내쉬며 뭉쳐있던 목과 어깨를 부드럽게 돌려주고, 온몸의 근육이 이완되는 찌릿한 감각에만 온전히 집중해 봅니다.",
        durationMinutes = 5
    ),
    RecommendedRecipe(
        title = "눈 감고 1분 명상",
        description = "화면을 끄고 가만히 눈을 감은 채, 들고나는 숨소리에만 온전히 집중해 봅니다.",
        durationMinutes = 1
    ),
    RecommendedRecipe(
        title = "딱 10초 동안 제자리 털기",
        description = "제자리 일어서서 손끝과 발끝, 그리고 온몸을 온 힘을 다해 자잘하게 탈탈탈 털어내며 몸의 모든 긴장과 걱정을 날려 보냅니다.",
        durationMinutes = 1
    ),
    RecommendedRecipe(
        title = "오감 그라운딩 실행하기",
        description = "현재의 감각에 집중하며 마음을 차분히 가라앉히는 5단계 불안 대처 수행법입니다. 현재 눈에 보이는 것 5가지, 촉각 4가지, 소리 3가지, 냄새 2가지, 맛 1가지를 차례로 천천히 찾아보며 현실에 집중해 봅니다.",
        durationMinutes = 5
    ),
    RecommendedRecipe(
        title = "찬물 한 컵 마시기",
        description = "시원한 물 한 컵을 준비해 목 넘김과 차가운 온도를 천천히 음미하며 마십니다.",
        durationMinutes = 2
    ),
    RecommendedRecipe(
        title = "시원한 물로 세수하기",
        description = "화장실로 가서 찬물로 세수를 하거나 손을 깨끗이 씻으며 기분을 리프레시합니다.",
        durationMinutes = 3
    ),
    RecommendedRecipe(
        title = "룸 스프레이 뿌리기",
        description = "공간에 좋아하는 향이나 마음이 편안해지는 아로마 스프레이를 분사하고 향을 깊게 들이마십니다.",
        durationMinutes = 1
    ),
    RecommendedRecipe(
        title = "가볍게 동네 산책하기",
        description = "편한 신발을 신고 밖으로 나가 가볍게 동네 한 바퀴를 걸으며 바깥 공기를 쐽니다.",
        durationMinutes = 15
    ),
    RecommendedRecipe(
        title = "창문 열고 환기하기",
        description = "창문을 활짝 열고 바깥 풍경을 바라보며 시원한 바람을 3번 크게 들이마십니다.",
        durationMinutes = 3
    ),
    RecommendedRecipe(
        title = "책상 정리정돈하기",
        description = "눈앞에 흩어진 펜, 종이, 쓰레기 등을 제자리에 치우며 주변 환경을 깔끔하게 정돈합니다.",
        durationMinutes = 5
    ),
    RecommendedRecipe(
        title = "귀여운 동물 영상 보기",
        description = "귀여운 강아지나 고양이 등 귀여운 동물이 나오는 짧은 클립 영상을 보며 소소하게 힐링합니다.",
        durationMinutes = 5
    ),
    RecommendedRecipe(
        title = "지금 기분에 어울리는 노래 찾기",
        description = "평소 자주 쓰는 음원 앱이나 유튜브를 켜고, 내 마음을 대변해 주거나 혹은 지금 기분을 완전히 환기해 줄 수 있는 노래 딱 1곡을 골라 가사에 집중하며 끝까지 감상합니다.",
        durationMinutes = 5
    ),
    RecommendedRecipe(
        title = "좋아하는 음악 듣기",
        description = "마음이 편안해지는 잔잔한 음악이나 평소 좋아하는 최애 곡을 1곡 끝까지 감상합니다.",
        durationMinutes = 5
    ),
    RecommendedRecipe(
        title = "가사 없이 빗소리만 듣기",
        description = "유튜브나 음원 앱에서 'ASMR 장작 타는 소리'나 '잔잔한 빗소리'를 검색해 틀어두고, 아무 생각 없이 소리의 결에만 귀를 기울입니다.",
        durationMinutes = 5
    ),
    RecommendedRecipe(
        title = "나만의 플레이리스트 만들기/재정비하기",
        description = "들으면 무조건 마음이 편안해지거나 든든해지는 노래들을 모아 새 플레이리스트를 만들고 이름을 붙여줍니다. 또는 이미 만든 플레이리스트를 다시 들어보며 추가/수정으로 재정비합니다.",
        durationMinutes = 15
    ),
    RecommendedRecipe(
        title = "마음에 꽂히는 책 문장 필사하기",
        description = "근처에 있는 책이나 좋아하는 시집을 아무 페이지나 펼쳐서, 마음에 닿는 문장 딱 한 줄을 정성스럽게 종이에 꾹꾹 눌러 적어봅니다.",
        durationMinutes = 3
    ),
    RecommendedRecipe(
        title = "오늘의 날씨를 아주 자세히 묘사하기",
        description = "메모장을 켜고 단순히 '맑음', '흐림' 대신 유리창에 빛이 은은하게 비치는 오후, 바람이 눅눅하게 부는 날처럼 소설가처럼 날씨를 한 문장으로 적어봅니다.",
        durationMinutes = 3
    ),
    RecommendedRecipe(
        title = "창밖 하늘 사진 예쁘게 한 장 찍기",
        description = "창가로 다가가 지금 이 순간의 하늘이나 바깥 풍경을 가장 감성적인 구도로 찰칵 사진에 담아 앨범에 보관합니다.",
        durationMinutes = 2
    ),
    RecommendedRecipe(
        title = "따뜻한 물로 샤워하기",
        description = "따뜻한 온수를 틀고 물이 피부에 닿는 촉감과 온기에 집중해 보세요. 좋아하는 향의 거품으로 온몸을 가볍게 마사지하며 오늘 하루 쌓인 복잡한 생각과 긴장을 물줄기와 함께 흘려보냅니다.",
        durationMinutes = 20
    ),
    RecommendedRecipe(
        title = "향기로운 바디로션으로 마사지하기",
        description = "샤워 후 좋아하는 향의 바디로션을 손바닥에 덜어 온기를 낸 뒤, 발끝부터 종아리, 어깨까지 고생한 내 몸을 정성스럽게 쓸어내리며 발라줍니다.",
        durationMinutes = 5
    ),
    RecommendedRecipe(
        title = "머리 빗질하며 생각 엉킴 풀기",
        description = "거울 앞에 앉아 끝이 둥근 빗으로 두피를 톡톡 두드려 마사지한 뒤, 엉킨 머리카락을 정성스레 빗어내립니다. 머릿속 엉킨 생각들도 함께 차분히 정리되는 것에 집중합니다.",
        durationMinutes = 3
    ),
    RecommendedRecipe(
        title = "따뜻한 물에 족욕하기",
        description = "대야에 따뜻한 물을 받아 발을 담그고 가만히 앉아봅니다. 발끝에서부터 온몸으로 서서히 퍼져나가는 따뜻한 혈류에 집중해 보세요.",
        durationMinutes = 15
    ),
    RecommendedRecipe(
        title = "마스크팩 얹고 누워있기",
        description = "냉장고에 넣어둔 시원한 마스크팩을 얼굴에 착 붙이고, 팩이 닿는 시원한 감각을 느끼며 침대에 편안히 누워 온몸의 힘을 빼봅니다.",
        durationMinutes = 15
    ),
    RecommendedRecipe(
        title = "따뜻한 차 마시기",
        description = "좋아하는 따뜻한 티백이나 음료를 우려내어 향을 맡고 천천히 한 모금씩 마십니다.",
        durationMinutes = 10
    ),
    RecommendedRecipe(
        title = "따뜻한 우유나 두유 마시기",
        description = "따뜻하게 데운 우유나 두유를 머그잔에 담아, 컵을 양손으로 감싸 쥐어 온기를 먼저 느낀 뒤 고소한 맛을 음미하며 천천히 마십니다.",
        durationMinutes = 5
    ),
    RecommendedRecipe(
        title = "나를 위한 비타민 충전 시간",
        description = "상큼한 비타민이나 영양제 한 알을 꺼내어 내 몸에 좋은 에너지를 선물한다는 마음으로 시원한 물과 함께 천천히 삼킵니다.",
        durationMinutes = 1
    )
)