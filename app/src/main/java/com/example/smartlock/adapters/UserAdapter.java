package com.example.smartlock.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.smartlock.R;
import com.example.smartlock.models.User;
import java.util.List;

public class UserAdapter extends BaseAdapter {

    private Context context;
    private List<User> users;
    private User currentUser;
    private LayoutInflater inflater;

    public UserAdapter(Context context, List<User> users, User currentUser) {
        this.context = context;
        this.users = users;
        this.currentUser = currentUser;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_user, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tv_name);
            holder.tvPhone = convertView.findViewById(R.id.tv_phone);
            holder.tvRole = convertView.findViewById(R.id.tv_role);
            holder.tvBiometric = convertView.findViewById(R.id.tv_biometric);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = users.get(position);

        holder.tvName.setText(user.getFullName());
        holder.tvPhone.setText(user.getPhoneNumber());

        // Hiển thị vai trò
        if (user.isOwner()) {
            holder.tvRole.setText("👑 Chủ sở hữu");
            holder.tvRole.setTextColor(context.getColor(R.color.orange));
        } else {
            holder.tvRole.setText("👤 Thành viên");
            holder.tvRole.setTextColor(context.getColor(R.color.text_secondary));
        }

        // Hiển thị trạng thái xác thực
        StringBuilder bio = new StringBuilder();
        if (user.hasFingerprint()) bio.append("🔐 Vân tay ");
        if (user.hasFace()) bio.append("😀 Khuôn mặt ");
        if (!user.hasFingerprint() && !user.hasFace()) bio.append("⚙️ Chưa thiết lập");

        holder.tvBiometric.setText(bio.toString());

        return convertView;
    }

    static class ViewHolder {
        TextView tvName, tvPhone, tvRole, tvBiometric;
    }
}