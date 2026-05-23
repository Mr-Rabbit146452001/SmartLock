package com.example.smartlock;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;

@SuppressWarnings("deprecation")
public class FaceUnlockActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int CAMERA_PERMISSION_CODE = 100;
    
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private boolean isPreviewRunning = false;
    
    private View viewPulse1, viewPulse2, viewScanLine;
    private ProgressBar pbScanning;
    private TextView tvStatus, tvInstruction;
    private ImageButton btnBack;
    
    private ObjectAnimator scanAnimator;
    private ObjectAnimator pulseAnimator1, pulseAnimator2;
    private ValueAnimator progressAnimator;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_unlock);

        initViews();
        setupListeners();
        setupAnimations();

        // Kiểm tra và yêu cầu quyền Camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED) {
            startFaceScanningFlow();
        } else {
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void initViews() {
        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        
        viewPulse1 = findViewById(R.id.view_pulse1);
        viewPulse2 = findViewById(R.id.view_pulse2);
        viewScanLine = findViewById(R.id.view_scan_line);
        pbScanning = findViewById(R.id.pb_scanning);
        tvStatus = findViewById(R.id.tv_status);
        tvInstruction = findViewById(R.id.tv_instruction);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void setupAnimations() {
        // 1. Hiệu ứng laser quét chạy lên xuống
        scanAnimator = ObjectAnimator.ofFloat(viewScanLine, "translationY", 0f, 680f);
        scanAnimator.setDuration(2000);
        scanAnimator.setRepeatMode(ValueAnimator.REVERSE);
        scanAnimator.setRepeatCount(ValueAnimator.INFINITE);
        scanAnimator.setInterpolator(new LinearInterpolator());

        // 2. Hiệu ứng vòng tròn xung mạch 1 (Pulsing)
        pulseAnimator1 = ObjectAnimator.ofFloat(viewPulse1, "scaleX", 1.0f, 1.25f);
        pulseAnimator1.setDuration(1500);
        pulseAnimator1.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator1.setRepeatMode(ValueAnimator.RESTART);
        
        ObjectAnimator pulseAlpha1 = ObjectAnimator.ofFloat(viewPulse1, "alpha", 1.0f, 0.0f);
        pulseAlpha1.setDuration(1500);
        pulseAlpha1.setRepeatCount(ValueAnimator.INFINITE);
        pulseAlpha1.setRepeatMode(ValueAnimator.RESTART);

        // 3. Hiệu ứng vòng tròn xung mạch 2 (Độ trễ 750ms so với vòng 1)
        pulseAnimator2 = ObjectAnimator.ofFloat(viewPulse2, "scaleX", 1.0f, 1.25f);
        pulseAnimator2.setDuration(1500);
        pulseAnimator2.setStartDelay(750);
        pulseAnimator2.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator2.setRepeatMode(ValueAnimator.RESTART);

        ObjectAnimator pulseAlpha2 = ObjectAnimator.ofFloat(viewPulse2, "alpha", 1.0f, 0.0f);
        pulseAlpha2.setDuration(1500);
        pulseAlpha2.setStartDelay(750);
        pulseAlpha2.setRepeatCount(ValueAnimator.INFINITE);
        pulseAlpha2.setRepeatMode(ValueAnimator.RESTART);

        // Chạy các animation vòng quét
        scanAnimator.start();
        pulseAnimator1.start();
        pulseAlpha1.start();
        pulseAnimator2.start();
        pulseAlpha2.start();
    }

    private void startFaceScanningFlow() {
        tvStatus.setText("ĐANG PHÂN TÍCH KHUÔN MẶT...");
        
        // Giả lập tiến trình quét khuôn mặt và phân tích AI (3 giây)
        progressAnimator = ValueAnimator.ofInt(0, 100);
        progressAnimator.setDuration(3000);
        progressAnimator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            pbScanning.setProgress(progress);
            
            if (progress < 40) {
                tvStatus.setText("ĐANG PHÂN TÍCH ĐIỂM HÌNH HỌC...");
            } else if (progress < 75) {
                tvStatus.setText("ĐANG SO KHỚP CƠ SỞ DỮ LIỆU...");
            } else if (progress < 95) {
                tvStatus.setText("XÁC MINH DANH TÍNH...");
            } else {
                tvStatus.setText("NHẬN DIỆN THÀNH CÔNG!");
            }
        });
        
        progressAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                super.onAnimationEnd(animation);
                onFaceUnlockSuccess();
            }
        });
        
        progressAnimator.start();
    }

    private void onFaceUnlockSuccess() {
        // Gửi lệnh mở khóa qua Firebase
        FirebaseManager.getInstance().getDatabaseRef()
                .child("command")
                .child("unlock")
                .setValue(true)
                .addOnSuccessListener(aVoid -> {
                    FirebaseManager.getInstance().addLog("Mở khóa", "Nhận diện khuôn mặt (App)", true);
                    Toast.makeText(FaceUnlockActivity.this, "🔓 Nhận diện thành công! Đang mở cửa...", Toast.LENGTH_SHORT).show();
                    
                    // Chuyển trực tiếp sang HomeActivity
                    Intent intent = new Intent(FaceUnlockActivity.this, HomeActivity.class);
                    intent.putExtra("device_id", "default_device");
                    intent.putExtra("device_name", "Smart Lock ESP32");
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FaceUnlockActivity.this, "Lỗi kết nối Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    tvStatus.setText("LỖI KẾT NỐI!");
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startFaceScanningFlow();
                // Khởi động lại camera preview
                surfaceDestroyed(surfaceHolder);
                surfaceCreated(surfaceHolder);
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền Camera để nhận diện khuôn mặt!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    // ================= CAMERA LIFECYCLE MANAGEMENT =================

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            // Mở camera trước
            int frontCameraId = getFrontCameraId();
            if (frontCameraId != -1) {
                camera = Camera.open(frontCameraId);
            } else {
                camera = Camera.open(); // Mở camera mặc định nếu không tìm thấy cam trước
            }
            
            camera.setPreviewDisplay(holder);
            // Xoay preview dọc (90 độ) cho phù hợp giao diện chân dung
            camera.setDisplayOrientation(90);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể mở camera!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) {
            if (isPreviewRunning) {
                camera.stopPreview();
            }
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                isPreviewRunning = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    private int getFrontCameraId() {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
            isPreviewRunning = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanAnimator != null) scanAnimator.cancel();
        if (pulseAnimator1 != null) pulseAnimator1.cancel();
        if (pulseAnimator2 != null) pulseAnimator2.cancel();
    }
}
