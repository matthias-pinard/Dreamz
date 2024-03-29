package com.matthias.dreamz.ui.screen.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.matthias.dreamz.R
import com.matthias.dreamz.data.model.DreamDay
import com.matthias.dreamz.ui.screen.Screen
import com.matthias.dreamz.ui.widget.BackNavButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExperimentalPagerApi
@Composable
fun CalendarScreen(calendarViewModel: CalendarViewModel, navController: NavController) {
    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE - 1)

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(id = R.string.calendar)) }, navigationIcon = {
            BackNavButton(navController)
        })
    }) {
        HorizontalPager(state = pagerState, count = Int.MAX_VALUE) { page ->
            Calendar(
                calendarViewModel = calendarViewModel,
                monthOffset = Int.MAX_VALUE - 1 - page,
                onClick = { dreamDay ->
                    if (dreamDay != null) {
                        navController.navigate(Screen.ViewDream.createRoute(dreamDay.uid))
                    }
                })

        }
    }
}

@Composable
fun Calendar(calendarViewModel: CalendarViewModel, monthOffset: Int, onClick: (DreamDay?) -> Unit) {
    val pageDate = LocalDate.now().minusMonths(monthOffset.toLong())

    val dreams =
        calendarViewModel.getDreams(pageDate.year, pageDate.monthValue)
            .collectAsState(initial = hashMapOf()).value
    val firstDay = LocalDate.of(pageDate.year, pageDate.month, 1)
    val nbDays = firstDay.plusMonths(1).minusDays(1).dayOfMonth
    val nbWeek = nbDays / 7
    val firstDayOfWeek = firstDay.dayOfWeek.value - 1
    val currentDay =
        if (firstDay.month == LocalDate.now().month && firstDay.year == LocalDate.now().year)
            LocalDate.now().dayOfMonth
        else
            nbDays
    val daysLabel = stringArrayResource(id = R.array.week_days)
    val nbDream = dreams.values.size
    Column {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                pageDate.format(DateTimeFormatter.ofPattern("MMM yyyy")),
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            for (i in 0..6) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(daysLabel[i], style = MaterialTheme.typography.h6)
                }
            }
        }
        for (i in 0..nbWeek + 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (j in 0..6) {
                    val day = i * 7 + j - (firstDayOfWeek - 1)
                    val dream =
                        dreams[firstDay.plusDays(day.toLong() - 1)]

                    if (day <= 0 || day > nbDays) {
                        CalendarDay("N", DayState.INACTIVE, onClick = {})
                    } else {
                        val state =
                            if (day <= currentDay) {
                                if (dream != null)
                                    DayState.DREAM
                                else
                                    DayState.ACTIVE
                            } else {
                                DayState.FUTURE
                            }
                        CalendarDay("$day", state, onClick = {
                            onClick(dream)
                        })
                    }
                }
            }
        }

        Text(stringResource(id = R.string.monthly_dream_message, nbDream))
    }
}

@Composable
fun CalendarDay(text: String, state: DayState, onClick: () -> Unit) {
    val elevation = 15.dp

    val color = when (state) {
        DayState.INACTIVE -> MaterialTheme.colors.background.copy(alpha = 0f)
        DayState.FUTURE, DayState.ACTIVE -> MaterialTheme.colors.background
        DayState.DREAM -> MaterialTheme.colors.primary
    }
    val alpha = when (state) {
        DayState.ACTIVE, DayState.DREAM -> 1f
        DayState.FUTURE -> 0.4f
        DayState.INACTIVE -> 0f
    }
    CompositionLocalProvider(LocalContentAlpha provides alpha) {
        Surface(
            color = color,
            elevation = elevation,
            shape = CircleShape,
            modifier = Modifier
                .size(40.dp)
                .clickable(
                    enabled = state == DayState.DREAM,
                    onClick = {
                        onClick()
                    }),
            contentColor = MaterialTheme.colors.onBackground,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text, style = MaterialTheme.typography.h6)
            }
        }
    }
}

enum class DayState {
    INACTIVE,
    FUTURE,
    ACTIVE,
    DREAM
}