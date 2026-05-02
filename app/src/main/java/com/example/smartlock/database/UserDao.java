package com.example.smartlock.database;

import androidx.room.*;
import com.example.smartlock.models.User;
import java.util.List;

@Dao
public interface UserDao {

    // Lấy tất cả người dùng
    @Query("SELECT * FROM users ORDER BY fullName ASC")
    List<User> getAllUsers();

    // Lấy người dùng theo ID
    @Query("SELECT * FROM users WHERE userId = :userId")
    User getUserByUserId(String userId);

    // Lấy người dùng theo ID số
    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);

    // Lấy chủ sở hữu
    @Query("SELECT * FROM users WHERE role = 'owner' LIMIT 1")
    User getOwner();

    // Lấy tất cả thành viên
    @Query("SELECT * FROM users WHERE role = 'member'")
    List<User> getMembers();

    // Tìm kiếm người dùng
    @Query("SELECT * FROM users WHERE fullName LIKE '%' || :query || '%'")
    List<User> searchUsers(String query);

    // Lấy người dùng có vân tay
    @Query("SELECT * FROM users WHERE fingerprintTemplate IS NOT NULL")
    List<User> getUsersWithFingerprint();

    // Lấy người dùng có khuôn mặt
    @Query("SELECT * FROM users WHERE faceEmbedding IS NOT NULL")
    List<User> getUsersWithFace();

    // Thêm người dùng
    @Insert
    long insertUser(User user);

    // Cập nhật người dùng
    @Update
    void updateUser(User user);

    // Xóa người dùng
    @Delete
    void deleteUser(User user);

    // Xóa người dùng theo ID
    @Query("DELETE FROM users WHERE id = :userId")
    void deleteUserById(int userId);

    // Kiểm tra có chủ sở hữu chưa
    @Query("SELECT COUNT(*) FROM users WHERE role = 'owner'")
    int getOwnerCount();
}