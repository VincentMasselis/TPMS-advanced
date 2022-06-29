package com.masselis.tpmsadvanced.interfaces.viewmodel.utils

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner

// Originally inspired by https://proandroiddev.com/dagger-tips-leveraging-assistedinjection-to-inject-viewmodels-with-savedstatehandle-and-93fe009ad874

inline fun <reified T : ViewModel> createViewModelFactory(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle?,
    crossinline creator: (SavedStateHandle) -> T
): ViewModelProvider.Factory =
    object : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T = creator(handle) as T
    }

inline fun <H : ViewModelStoreOwner, S : SavedStateRegistryOwner, reified T : ViewModel> createSavedStateViewModel(
    key: String? = null,
    defaultArgs: Bundle? = null,
    viewModelOwner: H,
    savedStateRegistryOwner: S,
    crossinline creator: (SavedStateHandle) -> T
): T =
    ViewModelProvider(
        viewModelOwner,
        createViewModelFactory(savedStateRegistryOwner, defaultArgs, creator)
    ).run {
        if (key != null) get(key, T::class.java)
        else get()
    }

@Composable
inline fun <reified T : ViewModel> savedStateViewModel(
    key: String? = null,
    defaultArgs: Bundle? = null,
    crossinline creator: (SavedStateHandle) -> T
) = createSavedStateViewModel(
    key,
    defaultArgs,
    LocalViewModelStoreOwner.current!!,
    LocalSavedStateRegistryOwner.current,
    creator
)