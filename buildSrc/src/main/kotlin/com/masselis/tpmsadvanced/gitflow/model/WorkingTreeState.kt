package com.masselis.tpmsadvanced.gitflow.model

import java.io.Serializable

/**
 * The state of the working tree, split by the same categories `git status` uses, so a caller can
 * report exactly which bucket is dirty instead of a generic "working directory is not empty".
 */
internal data class WorkingTreeState(
    val staged: Set<String>,
    val unstaged: Set<String>,
    val untracked: Set<String>,
) : Serializable {

    fun isClean(): Boolean = staged.isEmpty() && unstaged.isEmpty() && untracked.isEmpty()

    /** Human-readable description of every dirty bucket, for error messages. */
    fun describeDirty(): String = buildList {
        if (staged.isNotEmpty()) add("staged: ${staged.joinToString()}")
        if (unstaged.isNotEmpty()) add("unstaged: ${unstaged.joinToString()}")
        if (untracked.isNotEmpty()) add("untracked: ${untracked.joinToString()}")
    }.joinToString("; ")
}
