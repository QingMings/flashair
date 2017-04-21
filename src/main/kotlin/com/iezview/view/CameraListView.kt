package com.iezview.view

import com.iezview.controller.solutionList
import com.iezview.model.Camera
import tornadofx.*

/**
 * Created by shishifanbuxie on 2017/4/11.
 * 相机列表
 */
class CameraListView : View("My View") {
    override val root = listview<Camera> {
        cellCache {CameraItemFragment(it).root}
        subscribe<solutionList> { event ->items.setAll(event.cameras)}
    }
}
