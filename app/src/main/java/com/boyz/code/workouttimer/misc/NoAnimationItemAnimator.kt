package com.boyz.code.workouttimer.misc

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SimpleItemAnimator


class NoAnimationItemAnimator : SimpleItemAnimator() {
    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        dispatchRemoveFinished(holder)

        return false
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        dispatchAddFinished(holder)

        return false
    }

    override fun animateMove(holder: RecyclerView.ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        dispatchMoveFinished(holder)

        return false
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        dispatchChangeFinished(oldHolder, true)
        dispatchChangeFinished(newHolder, false)

        return false
    }

    override fun runPendingAnimations() {
        // stub
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        // stub
    }

    override fun endAnimations() {
        // stub
    }

    override fun isRunning(): Boolean {
        return false
    }
}