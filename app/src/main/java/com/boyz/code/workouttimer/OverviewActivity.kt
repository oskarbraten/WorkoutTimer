package com.boyz.code.workouttimer

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import com.boyz.code.workouttimer.misc.CustomAdapter
import com.boyz.code.workouttimer.misc.Workout

class OverviewActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val cards = ArrayList<Workout>()

//        cards.add(Workout("hehe", 200))

        val rv = findViewById<RecyclerView>(R.id.recyclerViewWorkout)
        rv.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)

        var adapter = CustomAdapter(cards)
        rv.adapter = adapter
    }
}
