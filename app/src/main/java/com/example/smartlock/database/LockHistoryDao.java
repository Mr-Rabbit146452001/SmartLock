package com.example.smartlock.database;

import androidx.room.*;
import com.example.smartlock.models.LockHistory;
import java.util.List;

@Dao
public interface LockHistoryDao {

    // Lấy tất cả lịch sử
    @Query("SELECT * FROM lock_history ORDER BY timestamp DESC")
    List<LockHistory> getAllHistory();

    // Lấy lịch sử theo người dùng
    @Query("SELECT * FROM lock_history WHERE userId = :userId ORDER BY timestamp DESC")
    List<LockHistory> getHistoryByUser(String userId);

    // Lấy lịch sử gần đây (giới hạn số lượng)
    @Query("SELECT * FROM lock_history ORDER BY timestamp DESC LIMIT :limit")
    List<LockHistory> getRecentHistory(int limit);

    // Lấy lịch sử theo hành động
    @Query("SELECT * FROM lock_history WHERE action = :action ORDER BY timestamp DESC")
    List<LockHistory> getHistoryByAction(String action);

    // Thêm lịch sử
    @Insert
    void insertHistory(LockHistory history);

    // Xóa lịch sử cũ (giữ lại 1000 gần nhất)
    @Query("DELETE FROM lock_history WHERE id NOT IN " +
            "(SELECT id FROM lock_history ORDER BY timestamp DESC LIMIT 1000)")
    void deleteOldHistory();

    // Xóa tất cả lịch sử
    @Query("DELETE FROM lock_history")
    void deleteAllHistory();
}