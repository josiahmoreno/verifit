package com.example.verifit.settings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class WritePermissionChecker(
    val permissionRequester: PermissionRequester,
    val requestMultiplePermissions:  ActivityResultLauncher<String>
) {
    suspend operator fun invoke(): Boolean {
        val response = CompletableDeferred<Boolean>()


        permissionRequester.resultInvoke = {
            response.complete(it)
        }
        requestMultiplePermissions.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return  response.await()
    }

    private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context,
                        permission) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    class PermissionRequester(val requestCallback: ActivityResultContracts.RequestPermission) {
        var resultInvoke: ((Boolean) -> Unit)? = null
        val requestResult: ActivityResultCallback<Boolean> = ActivityResultCallback<Boolean> {

            resultInvoke?.invoke(it)
        }




    }

}
