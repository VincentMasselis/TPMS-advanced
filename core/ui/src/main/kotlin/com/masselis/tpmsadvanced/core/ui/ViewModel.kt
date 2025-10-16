package com.masselis.tpmsadvanced.core.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import kotlin.reflect.KClass
import androidx.lifecycle.viewmodel.compose.viewModel as androidxViewModel


/**
 * Returns a key for a [ViewModel] like androidx does it but this method allows to customize it
 *
 * @see androidx.lifecycle.viewmodel.internal.ViewModelProviders.getDefaultKey
 */
public inline fun <reified T : ViewModel> KClass<T>.key(content: Map<String, String>): String {
    val canonicalName = requireNotNull(qualifiedName) {
        "Local and anonymous classes can not be ViewModels"
    }
    return StringBuilder()
        .append("com.masselis.tpmsadvanced.core.ui.key.class:$canonicalName")
        .apply {
            content.forEach { (key, value) ->
                append(", ")
                append("$key:$value")
            }
        }
        .toString()
}

/** Similar to [androidxViewModel] but with the support of [keyed] */
@Composable
public inline fun <reified VM : ViewModel> viewModel(
    keyed: Keyed,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    noinline initializer: CreationExtras.() -> VM
): VM = androidxViewModel(viewModelStoreOwner, VM::class.key(keyed), initializer)

/**
 * Similar to [androidxViewModel] but with the support of [keyed]. [HOST] is also sent as argument
 * to [initializer].
 */
@Composable
public inline fun <HOST, reified VM : ViewModel> HOST.viewModel(
    keyed: Keyed,
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
    noinline initializer: CreationExtras.(HOST) -> VM
): VM = androidxViewModel(viewModelStoreOwner, VM::class.key(keyed)) { initializer(this@viewModel) }

public typealias Keyed = Map<String, String>
