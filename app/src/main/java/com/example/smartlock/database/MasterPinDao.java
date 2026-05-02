package com.example.smartlock.database;

import androidx.room.*;
import com.example.smartlock.models.MasterPin;

@Dao
public interface MasterPinDao {

    // Lấy mã PIN (chỉ có 1 record)
    @Query("SELECT * FROM master_pin LIMIT 1")
    MasterPin getMasterPin();

    // Thêm mã PIN mới
    @Insert
    void insertMasterPin(MasterPin masterPin);

    // Cập nhật mã PIN
    @Update
    void updateMasterPin(MasterPin masterPin);

    // Xóa tất cả (dùng khi reset)
    @Query("DELETE FROM master_pin")
    void deleteAll();
}