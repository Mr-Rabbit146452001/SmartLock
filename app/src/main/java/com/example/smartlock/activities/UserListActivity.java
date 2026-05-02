package com.example.smartlock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartlock.R;
import com.example.smartlock.adapters.UserAdapter;
import com.example.smartlock.database.AppDatabase;
import com.example.smartlock.models.User;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private ListView lvUsers;
    private TextView tvEmpty;
    private Button btnAddUser, btnBack;
    private UserAdapter adapter;
    private AppDatabase database;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        database = AppDatabase.getInstance(this);
        currentUser = (User) getIntent().getSerializableExtra("current_user");

        initViews();
        loadUsers();
        setupListeners();
    }

    private void initViews() {
        lvUsers = findViewById(R.id.lv_users);
        tvEmpty = findViewById(R.id.tv_empty);
        btnAddUser = findViewById(R.id.btn_add_user);
        btnBack = findViewById(R.id.btn_back);
    }

    private void loadUsers() {
        new Thread(() -> {
            List<User> users = database.userDao().getAllUsers();
            runOnUiThread(() -> {
                if (users == null || users.isEmpty()) {
                    lvUsers.setVisibility(android.view.View.GONE);
                    tvEmpty.setVisibility(android.view.View.VISIBLE);
                } else {
                    lvUsers.setVisibility(android.view.View.VISIBLE);
                    tvEmpty.setVisibility(android.view.View.GONE);
                    adapter = new UserAdapter(this, users, currentUser);
                    lvUsers.setAdapter(adapter);

                    lvUsers.setOnItemClickListener((parent, view, position, id) -> {
                        User user = (User) parent.getItemAtPosition(position);
                        Toast.makeText(this, "Đã chọn: " + user.getFullName(), Toast.LENGTH_SHORT).show();
                        // TODO: Mở chi tiết người dùng
                    });
                }
            });
        }).start();
    }

    private void setupListeners() {
        btnAddUser.setOnClickListener(v -> {
            if (currentUser != null && currentUser.isOwner()) {
                Toast.makeText(this, "🔧 Tính năng đang phát triển\nSẽ sớm ra mắt!", Toast.LENGTH_LONG).show();
                // TODO: Mở AddEditUserActivity
            } else {
                Toast.makeText(this, "⚠️ Chỉ chủ sở hữu mới có quyền thêm người dùng", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }
}