package com.boyz.code.workouttimer.misc

import android.view.MotionEvent
import android.support.v7.widget.RecyclerView


class RecyclerViewDisableListener : RecyclerView.OnItemTouchListener {

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return true
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

    }
}