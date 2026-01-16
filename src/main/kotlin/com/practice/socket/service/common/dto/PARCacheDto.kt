package com.practice.socket.service.common.dto

import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest

data class PARCacheDto(
    val parRequestId: String
) {
    companion object {
        fun from(request: PreauthenticatedRequest)
            = PARCacheDto(request.id)
    }
}
