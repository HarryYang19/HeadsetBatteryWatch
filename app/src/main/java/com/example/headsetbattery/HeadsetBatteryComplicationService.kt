
package com.example.headsetbattery

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.LongTextComplicationData
import androidx.wear.watchface.complications.data.NoDataComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.RangedValueComplicationData
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationRequest

class HeadsetBatteryComplicationService : ComplicationDataSourceService() {

    override fun getPreviewData(type: ComplicationType): ComplicationData {
        val text = PlainComplicationText.Builder("85%").build()

        return when (type) {
            ComplicationType.RANGED_VALUE ->
                RangedValueComplicationData.Builder(
                    85f, 0f, 100f, text
                ).setTitle(
                    PlainComplicationText.Builder("耳机").build()
                ).build()

            ComplicationType.LONG_TEXT ->
                LongTextComplicationData.Builder(
                    PlainComplicationText.Builder("耳机 85%").build(),
                    PlainComplicationText.Builder("耳机电量").build()
                ).build()

            else ->
                ShortTextComplicationData.Builder(
                    text,
                    PlainComplicationText.Builder("耳机电量").build()
                ).build()
        }
    }

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener
    ) {
        val battery = readBattery()

        val data: ComplicationData =
            if (battery >= 0) {
                val text = PlainComplicationText.Builder("$battery%").build()

                when (request.complicationType) {
                    ComplicationType.RANGED_VALUE ->
                        RangedValueComplicationData.Builder(
                            battery.toFloat(), 0f, 100f, text
                        ).setTitle(
                            PlainComplicationText.Builder("耳机").build()
                        ).build()

                    ComplicationType.LONG_TEXT ->
                        LongTextComplicationData.Builder(
                            PlainComplicationText.Builder("耳机 $battery%").build(),
                            PlainComplicationText.Builder("耳机电量").build()
                        ).build()

                    else ->
                        ShortTextComplicationData.Builder(
                            text,
                            PlainComplicationText.Builder("耳机电量").build()
                        ).build()
                }
            } else {
                NoDataComplicationData()
            }

        listener.onComplicationData(data)
    }

    private fun readBattery(): Int {
        if (Build.VERSION.SDK_INT < 33) return -1

        if (
            checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return -1
        }

        return try {
            val manager =
                getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val adapter = manager.adapter ?: return -1

            // Android 标准 API 没有公开 BluetoothDevice.batteryLevel。
            // 暂不伪造电量，等待接入真实的耳机电量读取方案。
            val connectedDevices = adapter.bondedDevices
            if (connectedDevices.isEmpty()) -1 else -1
        } catch (_: SecurityException) {
            -1
        } catch (_: Exception) {
            -1
        }
    }
}
