package com.example.scentra.uicontroller.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.scentra.ScentraApp
import com.example.scentra.uicontroller.viewmodel.DetailViewModel
import com.example.scentra.uicontroller.viewmodel.EntryViewModel
import com.example.scentra.uicontroller.viewmodel.HistoryViewModel
import com.example.scentra.uicontroller.viewmodel.LoginViewModel
import com.example.scentra.uicontroller.viewmodel.ProfileViewModel
import com.example.scentra.uicontroller.viewmodel.UserDetailViewModel
import com.example.scentra.viewmodel.DashboardViewModel
import com.example.scentra.viewmodel.EditViewModel

object PenyediaViewModel {
    val Factory = viewModelFactory {
        initializer {
            LoginViewModel(
                scentraApplication().container.scentraRepository
            )
        }

        initializer {
            com.example.scentra.uicontroller.viewmodel.RegisterViewModel(
                scentraApplication().container.scentraRepository
            )
        }

        initializer {
            DashboardViewModel(scentraApplication().container.scentraRepository)
        }

        initializer {
            EntryViewModel(scentraApplication().container.scentraRepository)
        }

        initializer {
            DetailViewModel(scentraApplication().container.scentraRepository)
        }

        initializer {
            EditViewModel(scentraApplication().container.scentraRepository)
        }

        initializer {
            HistoryViewModel(scentraApplication().container.scentraRepository)
        }

        initializer {
            ProfileViewModel(scentraApplication().container.scentraRepository)
        }

        initializer {
            UserDetailViewModel(scentraApplication().container.scentraRepository)
        }

    }
}

fun CreationExtras.scentraApplication(): ScentraApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ScentraApp)