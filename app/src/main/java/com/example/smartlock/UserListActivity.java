package com.example.smartlock;

import com.example.smartlock.FirebaseManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smartlock.adapters.UserAdapter;
import com.example.smartlock.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    private ListView lvUsers;
    private TextView tvEmpty;
    private Button btnAddUser, btnBack;
    private UserAdapter adapter;
    private List<User> userList;
    private FirebaseManager firebaseManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        firebaseManager = FirebaseManager.getInstance();
        currentUser = (User) getIntent().getSerializableExtra("current_user");

        initViews();
        loadUsersFromFirebase();
        setupListeners();
    }

    private void initViews() {
        lvUsers = findViewById(R.id.lv_users);
        tvEmpty = findViewById(R.id.tv_empty);
        btnAddUser = findViewById(R.id.btn_add_user);
        btnBack = findViewById(R.id.btn_back);
        userList = new ArrayList<>();
    }

    private void loadUsersFromFirebase() {
        String userId = firebaseManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseManager.getDatabaseRef()
                .child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            User user = userSnapshot.getValue(User.class);
                            if (user != null) {
                                user.setUserId(userSnapshot.getKey());
                                userList.add(user);
                            }
                        }

                        if (userList.isEmpty()) {
                            lvUsers.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            lvUsers.setVisibility(View.VISIBLE);
                            tvEmpty.setVisibility(View.GONE);
                            adapter = new UserAdapter(UserListActivity.this, userList, currentUser);
                            lvUsers.setAdapter(adapter);

                            lvUsers.setOnItemClickListener((parent, view, position, id) -> {
                                User user = userList.get(position);
                                Toast.makeText(UserListActivity.this, "Đã chọn: " + user.getFullName(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserListActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupListeners() {
        btnAddUser.setOnClickListener(v -> {
            if (currentUser != null && currentUser.isOwner()) {
                Toast.makeText(this, "🔧 Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "⚠️ Chỉ chủ sở hữu mới có quyền thêm người dùng", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}