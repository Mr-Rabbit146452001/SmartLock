package com.example.smartlock;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartlock.adapters.DeviceAdapter;
import com.example.smartlock.models.Device;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class DeviceListActivity extends AppCompatActivity {

    private ListView lvDevices;
    private TextView tvEmpty;
    private Button btnAddDevice;
    private List<Device> deviceList;
    private DeviceAdapter adapter;
    private FirebaseManager firebaseManager;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        firebaseManager = FirebaseManager.getInstance();
        FirebaseUser user = firebaseManager.getCurrentUser();
        if (user != null) {
            currentUserId = user.getUid();
        }

        initViews();
        loadDevices();
        setupListeners();
    }

    private void initViews() {
        lvDevices = findViewById(R.id.lv_devices);
        tvEmpty = findViewById(R.id.tv_empty);
        btnAddDevice = findViewById(R.id.btn_add_device);
        deviceList = new ArrayList<>();
    }

    private void loadDevices() {
        if (currentUserId == null) return;

        firebaseManager.getDatabaseRef()
                .child("users")
                .child(currentUserId)
                .child("devices")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        deviceList.clear();
                        for (DataSnapshot deviceSnapshot : snapshot.getChildren()) {
                            Device device = deviceSnapshot.getValue(Device.class);
                            if (device != null) {
                                device.setDeviceId(deviceSnapshot.getKey());
                                deviceList.add(device);
                            }
                        }

                        if (deviceList.isEmpty()) {
                            lvDevices.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            lvDevices.setVisibility(View.VISIBLE);
                            tvEmpty.setVisibility(View.GONE);
                            adapter = new DeviceAdapter(DeviceListActivity.this, deviceList);
                            lvDevices.setAdapter(adapter);

                            lvDevices.setOnItemClickListener((parent, view, position, id) -> {
                                Device selectedDevice = deviceList.get(position);
                                Intent intent = new Intent(DeviceListActivity.this, DevicePinActivity.class);
                                intent.putExtra("device_id", selectedDevice.getDeviceId());
                                intent.putExtra("device_name", selectedDevice.getDeviceName());
                                intent.putExtra("device_serial", selectedDevice.getDeviceSerial());
                                startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(DeviceListActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupListeners() {
        btnAddDevice.setOnClickListener(v -> {
            Intent intent = new Intent(DeviceListActivity.this, AddDeviceActivity.class);
            startActivity(intent);
        });
    }
}