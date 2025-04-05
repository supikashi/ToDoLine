package com.spksh.todoline.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.spksh.todoline.ui.screens.CalendarScreen
import com.spksh.todoline.ui.screens.EventScreen
import com.spksh.todoline.ui.screens.MatrixScreen
import com.spksh.todoline.ui.screens.ScheduleSettingsScreen
import com.spksh.todoline.ui.screens.MainSettingsScreen
import com.spksh.todoline.ui.screens.StatisticsScreen
import com.spksh.todoline.ui.screens.TaskScreen
import com.spksh.todoline.ui.screens.TimeSlotScreen
import com.spksh.todoline.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("mytag", "recreate activity")
        enableEdgeToEdge()
        setContent {
            Log.i("mytag", "set content")
            ToDoLineApp()
        }
    }
}

@Composable
fun ToDoLineApp(
    mainViewModel: MainViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    LaunchedEffect(Unit) {
        mainViewModel.navigationEvents.collect { event ->
            when (event) {
                is MainViewModel.NavigationEvent.NavigateToTaskScreen -> {
                    navController.navigate("task_screen/${event.id}")
                }
                is MainViewModel.NavigationEvent.NavigateToEventScreen -> {
                    navController.navigate("event_screen/${event.id}")
                }
                MainViewModel.NavigationEvent.NavigateToCalendarScreen -> {
                    navController.navigateSingleTopTo("calendar")
                }
                MainViewModel.NavigationEvent.NavigateToMatrixScreen -> {
                    navController.navigateSingleTopTo("matrix")
                }
                MainViewModel.NavigationEvent.NavigateToSettings.Root -> {
                    navController.navigateSingleTopTo("settings")
                }
                MainViewModel.NavigationEvent.NavigateToSettings.ScheduleScreen -> {
                    navController.navigate("schedule")
                }
                MainViewModel.NavigationEvent.NavigateToSettings.StatisticsScreen -> {
                    navController.navigate("statistics")
                }
                MainViewModel.NavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is MainViewModel.NavigationEvent.NavigateToSettings.TimeSlotScreen -> {
                    navController.navigate("timeslot_screen/${event.id}")
                }
            }
        }
    }
    Log.i("mytag", "app recomp")
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    AppTheme {
        Scaffold(
            bottomBar = {
                BottomBar(
                    navBackStackEntry = navBackStackEntry,
                    openMatrixScreen = {mainViewModel.openMatrixScreen()},
                    openCalendarScreen = {mainViewModel.openCalendarScreen()},
                    openSettingsScreen = {mainViewModel.openSettingsScreen()}
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "matrix",
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                Log.i("mytag", "navhost recomp")
                composable(route = "matrix") {
                    Log.i("mytag", "navigate to matrix")
                    MatrixScreen(viewModel = mainViewModel)
                }
                composable(route = "calendar") {
                    Log.i("mytag", "navigate to calendar")
                    CalendarScreen(viewModel = mainViewModel)
                }
                composable(
                    route = "task_screen/{id}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.LongType }
                    )
                ) { navBackStackEntry ->
                    val taskId = navBackStackEntry.arguments?.getLong("id") ?: 0
                    Log.i("mytag", "navigate to task $taskId")
                    TaskScreen(
                        taskId = taskId,
                        viewModel = mainViewModel
                    )
                }
                composable(
                    route = "event_screen/{id}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.LongType }
                    )
                ) { navBackStackEntry ->
                    val eventId = navBackStackEntry.arguments?.getLong("id") ?: 0
                    Log.i("mytag", "navigate to event $eventId")
                    EventScreen(
                        eventId = eventId,
                        viewModel = mainViewModel
                    )
                }
                navigation(
                    route = "settings",
                    startDestination = "main"
                ) {
                    composable("main") {
                        MainSettingsScreen(
                            viewModel = mainViewModel
                        )
                    }
                    composable("schedule") {
                        ScheduleSettingsScreen(
                            viewModel = mainViewModel
                        )
                    }
                    composable("statistics") {
                        StatisticsScreen(
                            viewModel = mainViewModel
                        )
                    }
                    composable(
                        route = "timeslot_screen/{id}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.LongType }
                        )
                    ) { navBackStackEntry ->
                        val id = navBackStackEntry.arguments?.getLong("id") ?: 0
                        Log.i("mytag", "navigate to timeslot $id")
                        TimeSlotScreen(
                            id = id,
                            viewModel = mainViewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBar(
    navBackStackEntry: NavBackStackEntry?,
    openMatrixScreen: () -> Unit,
    openCalendarScreen: () -> Unit,
    openSettingsScreen: () -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(80.dp),
        containerColor = Color.Transparent
    ) {

        NavigationBarItem(
            selected = navBackStackEntry?.destination?.route == "calendar",
            onClick = openCalendarScreen,
            icon = { Icon(imageVector = Icons.Filled.DateRange, null) },
        )
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.route == "matrix",
            onClick = openMatrixScreen,
            icon = { Icon(imageVector = Icons.Filled.CheckCircle, null) },
        )
        NavigationBarItem(
            selected = false,
            onClick = openSettingsScreen,
            icon = { Icon(imageVector = Icons.Filled.Settings, null) },
        )
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
