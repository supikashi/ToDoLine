package com.spksh.todoline

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
import com.spksh.todoline.data.Task
import com.spksh.todoline.ui.screens.CalendarScreen
import com.spksh.todoline.ui.screens.MatrixScreen
import com.spksh.todoline.ui.screens.TaskScreen
import com.spksh.todoline.ui.theme.ToDoLineTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("mytag", "recreate activity")
        val app = application as App
        enableEdgeToEdge()
        setContent {
            Log.i("mytag", "set content")
            ToDoLineApp(mainViewModel = viewModel(factory = MainViewModelFactory(app.repository)))
        }
    }
}
@Composable
fun ToDoLineApp(
    mainViewModel: MainViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    Log.i("mytag", "app recomp")
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    ToDoLineTheme {
        Scaffold(
            bottomBar = {
                BottomBar(
                    navBackStackEntry = navBackStackEntry,
                    onNavigationItemClick = { navController.navigateSingleTopTo(it) }
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
                    MatrixScreen(
                        tasks_1 = mainViewModel.tasks_1,
                        tasks_2 = mainViewModel.tasks_2,
                        tasks_3 = mainViewModel.tasks_3,
                        tasks_4 = mainViewModel.tasks_4,
                        onCheckBox = { task, isDone ->
                            mainViewModel.updateTask(task.copy(isDone = isDone))
                        },
                        onTodoClick = {id -> navController.navigate("todo_screen/$id")},
                        onAddButton = {
                            val id = mainViewModel.addTask()
                            navController.navigate("todo_screen/$id")

                        }
                    )
                }
                composable(route = "calendar") {
                    Log.i("mytag", "navigate to calendar")
                    CalendarScreen()
                }
                composable(
                    route = "todo_screen/{id}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.IntType }
                    )
                ) { navBackStackEntry ->
                    Log.i("mytag", "navigate to task")
                    val todoId = navBackStackEntry.arguments?.getInt("id", 0) ?: 0
                    val task = mainViewModel.findTaskById(todoId)
                    Log.i("deletecheck", task.toString())
                    TaskScreen(
                        task = task,
                        onNameChanged = {name ->
                            val newTask = task?.copy(name = name)
                            newTask?.let {
                                Log.i("mytag", "name changed")
                                mainViewModel.updateTask(it)
                            }
                        },
                        onDescriptionChanged = { description ->
                            val newTask = task?.copy(description = description)
                            newTask?.let {
                                Log.i("mytag", "description changed")
                                mainViewModel.updateTask(it)
                            }
                        },
                        onDeleted = {
                            navController.popBackStack()
                            task?.let {
                                mainViewModel.deleteTask(it)
                            }
                        },
                        onBackClick = {navController.popBackStack()},
                        onImportanceChanged = { newImp ->
                            Log.i("mytag", "importance changed")
                            val newTask = task?.copy(importance = newImp.toInt())
                            newTask?.let {
                                Log.i("mytag", "importance changed")
                                mainViewModel.updateTask(it)
                            }
                        },
                        onUrgencyChanged = { newUrg ->
                            Log.i("mytag", "urg changed")
                            val newTask = task?.copy(urgency = newUrg.toInt())
                            newTask?.let {
                                Log.i("mytag", "unrgency changed")
                                mainViewModel.updateTask(it)
                            }
                        },
                        onDeadlineChanged = { newDeadline ->
                            val newTask = task?.copy(deadline = newDeadline)
                            newTask?.let {
                                Log.i("mytag", "deadline changed")
                                mainViewModel.updateTask(it)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BottomBar(
    navBackStackEntry: NavBackStackEntry?,
    onNavigationItemClick: (route: String) -> Unit,
) {
    NavigationBar(
        modifier = Modifier.height(80.dp),
        containerColor = Color.Transparent
    ) {

        NavigationBarItem(
            selected = navBackStackEntry?.destination?.route == "calendar",
            onClick = {onNavigationItemClick("calendar")},
            icon = { Icon(imageVector = Icons.Filled.DateRange, null) },
        )
        NavigationBarItem(
            selected = navBackStackEntry?.destination?.route == "matrix",
            onClick = {onNavigationItemClick("matrix")},
            icon = { Icon(imageVector = Icons.Filled.CheckCircle, null) },
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
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