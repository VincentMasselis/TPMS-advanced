package com.masselis.tpmsadvanced.feature.androidauto.endpoint

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.validation.HostValidator
import com.masselis.tpmsadvanced.feature.androidauto.endpoint.Session as TpmsAdvancedSession

internal class CarAppService : CarAppService() {
    override fun createHostValidator(): HostValidator = HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    override fun onCreateSession(info: SessionInfo): Session = TpmsAdvancedSession()
}
