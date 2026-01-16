package com.practice.socket.service.common

import com.oracle.bmc.objectstorage.ObjectStorage
import com.practice.socket.config.properties.OciProperties
import org.springframework.stereotype.Service

@Service
class FileService (
    private val objectStorage: ObjectStorage,
    private val ociProperties: OciProperties
) {


}
