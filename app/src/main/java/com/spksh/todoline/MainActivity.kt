package com.spksh.todoline

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spksh.todoline.data.Task
import com.spksh.todoline.screens.CalendarScreen
import com.spksh.todoline.screens.MatrixScreen
import com.spksh.todoline.screens.TaskScreen
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
    ToDoLineTheme {
        NavHost(
            navController = navController,
            startDestination = "matrix",
            modifier = Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background)
        ) {
            Log.i("mytag", "navhost recomp")
            composable(route = "matrix") {
                Log.i("mytag", "navigate to matrix")
                MatrixScreen(
                    tasks_1 = mainViewModel.tasks_1,
                    tasks_2 = mainViewModel.tasks_2,
                    tasks_3 = mainViewModel.tasks_3,
                    tasks_4 = mainViewModel.tasks_4,
                    onCheckBox = { id, isDone ->
                        val task = mainViewModel.findTaskById(id)?.copy(isDone = isDone)
                        if (task != null) {
                            mainViewModel.updateTask(task)
                        }
                    },
                    onTodoClick = {id -> navController.navigate("todo_screen/$id")},
                    onCalendar = {navController.navigateSingleTopTo("calendar")},
                    onAddButton = {
                        //navController.navigate("todo_screen/${task.id}")
                        mainViewModel.addTask(Task())
                    }
                )
            }
            composable(route = "calendar") {
                Log.i("mytag", "navigate to calendar")
                CalendarScreen(onMatrix = {navController.navigateSingleTopTo("matrix")})
            }
            composable(
                route = "todo_screen/{id}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType }
                )
            ) { navBackStackEntry ->
                Log.i("mytag", "navigate to task")
                val todoId = navBackStackEntry.arguments?.getInt("id", 0) ?: 0
                TaskScreen(
                    task = mainViewModel.findTaskById(todoId),
                    onNameChanged = {name ->
                        val task = mainViewModel.findTaskById(todoId)?.copy(name = name)
                        if (task != null) {
                            Log.i("mytag", "name changed")
                            mainViewModel.updateTask(task)
                        }
                    },
                    onDescriptionChanged = { description ->
                        val task = mainViewModel.findTaskById(todoId)?.copy(description = description)
                        if (task != null) {
                            Log.i("mytag", "description changed")
                            mainViewModel.updateTask(task)
                        }
                    },
                    onDeleted = {
                        navController.popBackStack()
                        mainViewModel.deleteTask(Task(id = todoId))
                    },
                    onBackClick = {navController.popBackStack()},
                    onImportanceChanged = { newImp ->
                        Log.i("mytag", "importance changed")
                        val task = mainViewModel.findTaskById(todoId)?.copy(importance = newImp.toInt())
                        if (task != null) {
                            Log.i("mytag", "importance changed")
                            mainViewModel.updateTask(task)
                        }
                    },
                    onUrgencyChanged = { newUrg ->
                        Log.i("mytag", "urg changed")
                        val task = mainViewModel.findTaskById(todoId)?.copy(urgency = newUrg.toInt())
                        if (task != null) {
                            Log.i("mytag", "unrgency changed")
                            mainViewModel.updateTask(task)
                        }
                    }
                )
            }
        }
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