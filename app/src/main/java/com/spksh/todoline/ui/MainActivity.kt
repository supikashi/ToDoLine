package com.spksh.todoline.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.spksh.todoline.data.DataStoreRepository
import com.spksh.todoline.ui.screens.CalendarScreen
import com.spksh.todoline.ui.screens.EventScreen
import com.spksh.todoline.ui.screens.MainSettingsScreen
import com.spksh.todoline.ui.screens.MatrixScreen
import com.spksh.todoline.ui.screens.ScheduleSettingsScreen
import com.spksh.todoline.ui.screens.StatisticsScreen
import com.spksh.todoline.ui.screens.TaskScreen
import com.spksh.todoline.ui.screens.TimeSlotScreen
import com.spksh.todoline.ui.theme.AppTheme
import com.spksh.todoline.ui.theme.backgroundDark
import com.spksh.todoline.ui.theme.backgroundLight
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.util.Locale

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DataStoreRepositoryEntryPoint {
    fun getDataStoreRepository(): DataStoreRepository
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(applySelectedAppLanguage(base))
    }

    private fun applySelectedAppLanguage(context: Context): Context {

        val dataStoreRepository = EntryPointAccessors.fromApplication(
            context.applicationContext,
            DataStoreRepositoryEntryPoint::class.java
        ).getDataStoreRepository()
        val language = runBlocking { dataStoreRepository.getLanguage() ?: true }
        val locale = Locale(if (language) "en" else "ru")
        val newConfig = Configuration(context.resources.configuration)
        Locale.setDefault(locale)
        newConfig.setLocale(locale)
        return context.createConfigurationContext(newConfig)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("mytag", "recreate activity")
        val dataStoreRepository = EntryPointAccessors.fromApplication(
            this.applicationContext,
            DataStoreRepositoryEntryPoint::class.java
        ).getDataStoreRepository()
        val theme = runBlocking { dataStoreRepository.settingsDataFlow.firstOrNull()?.isDarkTheme ?: true }
        enableEdgeToEdge()
        setContent {
            Log.i("mytag", "set content")
            AppTheme(
                darkTheme = theme
            ) {
                ToDoLineApp(mainViewModel = viewModel, theme)
            }
        }
    }
}

@Composable
fun ToDoLineApp(
    mainViewModel: MainViewModel = viewModel(),
    isDarkTheme: Boolean = true,
    navController: NavHostController = rememberNavController()
) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

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
    var color by remember { mutableStateOf<Color?>(null) }
    LaunchedEffect(isDarkTheme) {
        color = if (isDarkTheme) backgroundDark else backgroundLight
    }
    Log.i("mytag", "app recomp")
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    //AppTheme(
   //     darkTheme = uiState.settings.isDarkTheme
    //) {
        Scaffold(
            bottomBar = {
                BottomBar(
                    navBackStackEntry = navBackStackEntry,
                    openMatrixScreen = {mainViewModel.openMatrixScreen()},
                    openCalendarScreen = {mainViewModel.openCalendarScreen()},
                    openSettingsScreen = {mainViewModel.openSettingsScreen()}
                )
            },
            modifier = Modifier.fillMaxSize(),
            containerColor = color ?: MaterialTheme.colorScheme.primary,
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "matrix",
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    //.background(color = MaterialTheme.colorScheme.background)
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
    //}
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
