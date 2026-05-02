package com.example.smartlock.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.smartlock.R;
import com.example.smartlock.models.Device;
import java.util.List;

public class DeviceAdapter extends BaseAdapter {

    private Context context;
    private List<Device> devices;
    private LayoutInflater inflater;

    public DeviceAdapter(Context context, List<Device> devices) {
        this.context = context;
        this.devices = devices;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return devices.size(); }

    @Override
    public Object getItem(int position) { return devices.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_device, parent, false);
            holder = new ViewHolder();
            holder.tvDeviceName = convertView.findViewById(R.id.tv_device_name);
            holder.tvDeviceSerial = convertView.findViewById(R.id.tv_device_serial);
            holder.tvStatus = convertView.findViewById(R.id.tv_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Device device = devices.get(position);
        holder.tvDeviceName.setText(device.getDeviceName());
        holder.tvDeviceSerial.setText(device.getDeviceSerial());

        if (device.isOnline()) {
            holder.tvStatus.setText("● Online");
            holder.tvStatus.setTextColor(context.getColor(R.color.success));
        } else {
            holder.tvStatus.setText("○ Offline");
            holder.tvStatus.setTextColor(context.getColor(R.color.error));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvDeviceName, tvDeviceSerial, tvStatus;
    }
}