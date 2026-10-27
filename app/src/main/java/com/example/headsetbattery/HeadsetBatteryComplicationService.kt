package com.example.headsetbattery

import android.Manifest
import android.bluetooth.BluetoothDevice
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
    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationDataSourceService.ComplicationRequestListener
    ) {
        val battery = readBattery()
        val data: ComplicationData = if (battery >= 0) {
            val text = PlainComplicationText.Builder("$battery%").build()
            when (request.complicationType) {
                ComplicationType.RANGED_VALUE -> RangedValueComplicationData.Builder(
                    battery.toFloat(), 0f, 100f, text
                ).setTitle(PlainComplicationText.Builder("耳机").build()).build()
                ComplicationType.LONG_TEXT -> LongTextComplicationData.Builder(
                    PlainComplicationText.Builder("耳机 $battery%").build(),
                    PlainComplicationText.Builder("耳机电量").build()
                ).build()
                else -> ShortTextComplicationData.Builder(
                    text, PlainComplicationText.Builder("耳机电量").build()
                ).build()
            }
        } else {
            NoDataComplicationData()
        }
        listener.onComplicationData(data)
    }

    private fun readBattery(): Int {
        if (Build.VERSION.SDK_INT < 33) return -1
        if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return -1
        return try {
            val manager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val adapter = manager.adapter ?: return -1
            for (device: BluetoothDevice in adapter.bondedDevices) {
                val level = device.batteryLevel
                if (level >= 0) return level
            }
            -1
        } catch (_: Exception) { -1 }
    }
}
