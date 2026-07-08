package com.gominitta.android.presentation.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gominitta.android.presentation.main.BottomNavItem
import com.gominitta.android.ui.theme.Primary200
import com.gominitta.android.ui.theme.Primary400
import com.gominitta.android.ui.theme.Primary800

/**
 * 하단 탭 바 — 4개 메인 탭 전환. (Figma Color Styles 기준)
 * - 배경: Primary/200 #F3F0EB, 상단에 Primary/400 #D0C1AB 구분선(1dp).
 * - 선택: Primary/800 #534B42, 선택 인디케이터(배경 알약) 없음.
 * - 미선택: Primary/400 #D0C1AB.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GominittaBottomBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Column {
        HorizontalDivider(thickness = 1.dp, color = Primary400)
        CompositionLocalProvider(LocalRippleConfiguration provides null) {
            NavigationBar(containerColor = Primary200) {
                BottomNavItem.items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.label,
                                modifier = Modifier.size(32.dp),
                            )
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary800,
                            selectedTextColor = Primary800,
                            unselectedIconColor = Primary400,
                            unselectedTextColor = Primary400,
                            indicatorColor = Color.Transparent,
                        ),
                    )
                }
            }
        }
    }
}
