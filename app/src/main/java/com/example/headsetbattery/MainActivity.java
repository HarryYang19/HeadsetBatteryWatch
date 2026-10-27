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
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends Activity {
    private LinearLayout root;
    private static final int REQUEST_BT = 100;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(0);
        buildUi();
        if (Build.VERSION.SDK_INT >= 31 &&
                checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BT);
        } else {
            refresh();
        }
    }

    private void buildUi() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(24, 20, 24, 20);
        root.setBackgroundColor(Color.rgb(0, 0, 0));

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

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(requestCode, permissions, results);
        if (requestCode == REQUEST_BT) refresh();
    }

    private void refresh() {
        TextView subtitle = root.findViewWithTag("subtitle");
        TextView value = root.findViewWithTag("value");
        try {
            BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            Set<BluetoothDevice> devices = manager.getAdapter().getBondedDevices();
            int found = 0;
            String name = null;
            int battery = -1;
            for (BluetoothDevice device : devices) {
                int level = device.getBatteryLevel(); // Public API on Android 13+; may be unavailable for some headsets.
                if (level >= 0) {
                    found++;
                    name = device.getName();
                    battery = level;
                    break;
                }
            }
            if (battery >= 0) {
                subtitle.setText(name == null ? "已连接耳机" : name);
                value.setText(battery + "%");
            } else {
                subtitle.setText("未读取到耳机电量");
                value.setText("--%");
            }
        } catch (SecurityException | NullPointerException ex) {
            subtitle.setText("请允许蓝牙权限");
            value.setText("--%");
        } catch (Exception ex) {
            subtitle.setText("当前设备暂不支持读取");
            value.setText("--%");
        }
    }
}
