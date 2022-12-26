package com.siabucketup.ecotaxi.ui.camera

interface CameraNavigation {

    fun goToConfirmFromCamera()

    fun goToConfirmFromCamera(touchX: Double, touchY: Double)

    fun goToDemoFromCamera()

}