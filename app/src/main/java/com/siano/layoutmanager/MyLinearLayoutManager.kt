package com.siano.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class MyLinearLayoutManager : LinearLayoutManager {

    constructor(context: Context) : super(context) {
        recycleChildrenOnDetach = true
    }

    constructor(context: Context, horizontal: Int, b: Boolean) : super(context, horizontal, b) {
        recycleChildrenOnDetach = true
    }
}
