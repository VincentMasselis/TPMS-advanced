package com.masselis.tpmsadvanced.gitflow.model

import java.io.Serializable

/** Where HEAD currently points. */
internal sealed interface HeadState : Serializable {
    @JvmInline
    value class OnBranch(val branch: String) : HeadState    {
        init {
            require(branch.isNotBlank()) { "Branch name must not be blank" }
            require(branch.startsWith("refs/").not()) {
                "LocalBranch expects a short branch name, got a ref path: \"$branch\""
            }
            require(branch.startsWith("origin/").not()) {
                "\"$branch\" looks like a remote-tracking ref, not a local branch name. Use a plain branch name instead."
            }
        }
    }

    @JvmInline
    value class Detached(val sha: String) : HeadState
}
