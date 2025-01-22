package com.masselis.tpmsadvanced

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.validation.HostValidator

public class TpmsCarAppService : CarAppService() {
    @SuppressLint("PrivateResource")
    override fun createHostValidator(): HostValidator {
        if (applicationInfo.flags.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
        }

        return HostValidator.Builder(applicationContext)
            .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
            .build()
    }

    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        val session = TpmsCarAppSession(sessionInfo)
        //TODO: lifecycle, ...
        return session
    }
}