package com.example.aisplitwise

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.example.aisplitwise.data.local.Expense
import com.example.aisplitwise.data.local.Member
import com.example.aisplitwise.feature.dashboard.DashBoard
import com.example.aisplitwise.feature.dashboard.DashboardViewModel
import com.example.aisplitwise.feature.feature_create_group.CreateGroupScreen
import com.example.aisplitwise.feature.feature_expense_dialog.ExpenseDialog
import com.example.aisplitwise.feature.feature_expense_dialog.ExpenseDialogViewModel
import com.example.aisplitwise.feature.feature_ledger.LedgerScreen
import com.example.aisplitwise.feature.feature_login.LoginScreen
import com.example.aisplitwise.feature.feature_signup.SignUpScreen
import com.example.aisplitwise.feature.feature_splash.SplashScreen
import kotlinx.serialization.Serializable


@kotlinx.serialization.Serializable
object SplashRoute
@kotlinx.serialization.Serializable
object DashBoardRoute

@kotlinx.serialization.Serializable
object LoginScreenRoute

@kotlinx.serialization.Serializable
object SignUpScreenRoute

@kotlinx.serialization.Serializable
object CreateGroupRoute

@kotlinx.serialization.Serializable
data class LedgerRoute(val groupId:String="")

@Serializable
object ExpenseDialogRoute

@Composable
fun NavHostInitializer(navController: NavHostController) {
    NavHost(navController = navController, startDestination = SplashRoute) {
        composable<SplashRoute>(
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            SplashScreen(hiltViewModel(),navController)
        }

        composable<DashBoardRoute> (
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            val dashBoardViewModel = hiltViewModel<DashboardViewModel>()
            DashBoard(dashBoardViewModel,navController)
        }


        composable<LoginScreenRoute> (
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            LoginScreen( loginViewModel = hiltViewModel(),
                navController)
        }

        composable<SignUpScreenRoute>(
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            SignUpScreen( signUpViewModel = hiltViewModel(),
                navController = navController)
        }

        composable<CreateGroupRoute>(
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            CreateGroupScreen(
                navController = navController)
        }

        composable<LedgerRoute>(
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            LedgerScreen(
                navHostController = navController,
                ledgerViewModel = hiltViewModel(),
                navigateToExpenseDialog={
                    navController.currentBackStackEntry?.savedStateHandle?.set("Members_Key",it)
                    navController.navigate(ExpenseDialogRoute)
                })
        }
        composable<ExpenseDialogRoute>(       ) {

            ExpenseDialog(
                navHostController = navController,
                expenseViewModel = hiltViewModel<ExpenseDialogViewModel>(),
                members= navController.previousBackStackEntry?.savedStateHandle?.get<List<Member>>("Members_Key")?: emptyList()
            )
        }

    }
}


fun slideInOutTransition(
    duration: Int = 1000,
    slideDirection: SlideDirection
): Pair<() -> EnterTransition, () -> ExitTransition> {
    return when (slideDirection) {
        SlideDirection.LeftToRight -> {
            {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(duration,easing= LinearEasing))
            } to {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(duration,easing= LinearEasing))
            }
        }
        SlideDirection.RightToLeft -> {
            {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(duration,easing= LinearEasing))
            } to {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(duration,easing= LinearEasing))
            }
        }
    }
}

enum class SlideDirection {
    LeftToRight,
    RightToLeft
}

