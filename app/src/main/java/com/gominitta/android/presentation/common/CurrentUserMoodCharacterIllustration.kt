package com.gominitta.android.presentation.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.gominitta.android.ui.components.moodCharacterDrawableRes

/**
 * 로그인한 유저의 프로필 캐릭터를 가져와 감정 점수([score])에 맞는 일러스트를 보여준다.
 * 마이페이지 프로필 데이터 소스가 바뀌어도 이 파일 안에서만 대응하면 되도록,
 * 캐릭터 일러스트가 필요한 화면들은 이쪽을 사용한다.
 */
@Composable
fun CurrentUserMoodCharacterIllustration(
    score: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = "감정 표현 이미지",
    profileViewModel: ProfileImageViewModel = hiltViewModel(),
) {
    val profileIndex = profileViewModel.profileIndex
    if (profileIndex != null) {
        Image(
            painter = painterResource(moodCharacterDrawableRes(profileIndex, score)),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
    } else {
        Box(modifier = modifier)
    }
}
