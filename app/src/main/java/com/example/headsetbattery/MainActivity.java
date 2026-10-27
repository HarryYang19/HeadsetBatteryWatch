
package com.example.headsetbattery;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends Activity {
    private LinearLayout root;
    private static final int REQUEST_BT = 100;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(0);
        buildUi();

        if (Build.VERSION.SDK_INT >= 31 &&
                checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                        != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    REQUEST_BT
            );
        } else {
            refresh();
        }
    }

    private void buildUi() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(24, 20, 24, 20);
        root.setBackgroundColor(Color.BLACK);

        TextView title = new TextView(this);
        title.setText("耳机电量");
        title.setTextColor(Color.WHITE);
        title.setTextSize(17);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);

        TextView subtitle = new TextView(this);
        subtitle.setTag("subtitle");
        subtitle.setTextColor(Color.rgb(180, 180, 180));
        subtitle.setTextSize(12);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 8, 0, 0);

        TextView value = new TextView(this);
        value.setTag("value");
        value.setTextColor(Color.rgb(74, 222, 128));
        value.setTextSize(32);
        value.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        value.setGravity(Gravity.CENTER);
        value.setPadding(0, 12, 0, 0);

        TextView refresh = new TextView(this);
        refresh.setText("点按刷新");
        refresh.setTextColor(Color.rgb(160, 160, 160));
        refresh.setTextSize(11);
        refresh.setGravity(Gravity.CENTER);
        refresh.setPadding(0, 12, 0, 0);
        refresh.setOnClickListener(v -> refresh());

        root.addView(title, new LinearLayout.LayoutParams(-1, -2));
        root.addView(subtitle, new LinearLayout.LayoutParams(-1, -2));
        root.addView(value, new LinearLayout.LayoutParams(-1, -2));
        root.addView(refresh, new LinearLayout.LayoutParams(-1, -2));
        setContentView(root);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == REQUEST_BT) {
            if (Build.VERSION.SDK_INT < 31 ||
                    checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT)
                            == PackageManager.PERMISSION_GRANTED) {
                refresh();
            } else {
                showStatus("需要蓝牙连接权限", "--%");
            }
        }
    }

    private void showStatus(String subtitleText, String valueText) {
        TextView subtitle = root.findViewWithTag("subtitle");
        TextView value = root.findViewWithTag("value");
        subtitle.setText(subtitleText);
        value.setText(valueText);
    }

    private void refresh() {
        try {
            BluetoothManager manager =
                    (BluetoothManager) getSystemService(
                            Context.BLUETOOTH_SERVICE);

            if (manager == null || manager.getAdapter() == null) {
                showStatus("蓝牙不可用", "--%");
                return;
            }

            Set<BluetoothDevice> devices =
                    manager.getAdapter().getBondedDevices();

            if (devices.isEmpty()) {
                showStatus("没有已配对的蓝牙设备", "--%");
                return;
            }

            // Android 标准公开 API 不提供通用的耳机电量读取方法。
            // 不显示猜测值，避免将错误电量当作真实数据。
            showStatus("已配对 " + devices.size() + " 个设备，暂无法读取电量",
                    "--%");

        } catch (SecurityException ex) {
            showStatus("请允许蓝牙权限", "--%");
        } catch (Exception ex) {
            showStatus("读取蓝牙设备失败", "--%");
        }
    }
}
