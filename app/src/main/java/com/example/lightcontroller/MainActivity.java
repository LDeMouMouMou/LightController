package com.example.lightcontroller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.lightcontroller.BluetoothViewer.BluetoothServer;
import com.example.lightcontroller.fragmentSession.BrightnessFragment;
import com.example.lightcontroller.fragmentSession.LuxFragment;
import com.example.lightcontroller.fragmentSession.ConnectFragment;
import com.example.lightcontroller.fragmentSession.SwitcherFragment;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap originalBitmap;
    private Bitmap modifiedBitmap;
    private Canvas canvas;
    private Paint paint;
    private ColorMatrix colorMatrix;
    private Matrix matrix;
    private BottomNavigationBar bottomNavigationBar;
    private Fragment[] fragments;
    private boolean currentPowerState = false;
    private int currentBrightness = 200;
    private int currentLux = 4000;
    private int lastFragmentIndex;
    private BluetoothServer bluetoothServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewByIds();
        initImageViews();
        initFragments();
        bottomNavigationBarInit();
        // 默认为关闭状态
        updateBulbState();
        // 绑定Service
        bindService(new Intent(MainActivity.this, BluetoothServer.class), serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothServer.BluetoothBinder bluetoothBinder = (BluetoothServer.BluetoothBinder) service;
            bluetoothServer = bluetoothBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void findViewByIds() {
        imageView = findViewById(R.id.image_bulb);
        bottomNavigationBar = findViewById(R.id.bottomNaviBar);
    }

    private void initImageViews() {
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image_bulb)
                .copy(Bitmap.Config.ARGB_8888, true);
        modifiedBitmap = Bitmap.createBitmap(originalBitmap);
        canvas = new Canvas(modifiedBitmap);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        matrix = new Matrix();
        colorMatrix = new ColorMatrix();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(originalBitmap, matrix, paint);
        imageView.setImageBitmap(modifiedBitmap);
    }

    private void initFragments() {
        Fragment fragment1 = new SwitcherFragment();
        Fragment fragment2 = new BrightnessFragment();
        Fragment fragment3 = new LuxFragment();
        Fragment fragment4 = new ConnectFragment();
        fragments = new Fragment[]{fragment1, fragment2, fragment3, fragment4};
        lastFragmentIndex = 3;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentview, fragments[lastFragmentIndex])
                .show(fragments[lastFragmentIndex]).commit();
    }

    private void bottomNavigationBarInit() {
        bottomNavigationBar.setActiveColor(R.color.colorWhite);
        bottomNavigationBar.setBarBackgroundColor(R.color.colorMainBlue);
        bottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.icon_switch, "开关"))
                .addItem(new BottomNavigationItem(R.drawable.icon_bright, "亮度"))
                .addItem(new BottomNavigationItem(R.drawable.icon_lux, "色温"))
                .addItem(new BottomNavigationItem(R.drawable.icon_connect, "连接"));
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_SHIFTING);
        bottomNavigationBar.setFirstSelectedPosition(3);
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (position != lastFragmentIndex) {
                    switchFragment(lastFragmentIndex, position);
                    lastFragmentIndex = position;
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
        bottomNavigationBar.initialise();
    }

    private void switchFragment(int lastFragmentIndex, int toIndex) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.hide(fragments[lastFragmentIndex]);
        if (!fragments[toIndex].isAdded()) {
            fragmentTransaction.add(R.id.fragmentview, fragments[toIndex]);
        }
        fragmentTransaction.show(fragments[toIndex]).commitAllowingStateLoss();
    }

    @SuppressWarnings("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg != null) {
                String messageContent = msg.obj.toString();
                if (messageContent.contains("CHANGE BRIGHTNESS")) {
                    bottomNavigationBar.selectTab(1, false);
                    lastFragmentIndex = 1;
                    switchFragment(0, 1);
                }
                else if (messageContent.contains("CHANGE LUX")) {
                    bottomNavigationBar.selectTab(2, false);
                    lastFragmentIndex = 2;
                    switchFragment(0, 2);
                }
                else if (messageContent.contains("POWER ON/")) {
                    changeImageAlpha(100);
                    currentPowerState = true;
                }
                else if (messageContent.contains("POWER OFF/")) {
                    changeImageAlpha(10);
                    currentPowerState = false;
                }
                else if (messageContent.contains("BRIGHTNESS UPDATE/")) {
                    currentBrightness = Integer.valueOf(messageContent.substring(messageContent.indexOf("/")+1));
                }
                else if (messageContent.contains("LUX UPDATE/")) {
                    currentLux = Integer.valueOf(messageContent.substring(messageContent.indexOf("/")+1));
                }
                updateBulbState();
            }
        }
    };

    // 这个方法使用当前的开关状态、亮度、色温来实时下发指令及改变显示状态（图片亮度等）
    // 亮度色温其实是百分比，只是一个相对值，因为只做演示用
    private void updateBulbState() {
        if (currentPowerState) {
            changeImageAlpha(100);
            changeImageBrightness(currentBrightness/5);
            changeImageTemp(currentLux);
        }
        else {
            changeImageAlpha(10);
            changeImageBrightness(10);
            changeImageTemp(10);
        }
    }

    private void changeImageAlpha(int alpha) {
        colorMatrix.set(new float[]{
                1, 0, 0, 0, 0,
                0, 1, 0, 0, 0,
                0, 0, 1, 0, 0,
                0, 0, 0, alpha/50.0f, 0,
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(originalBitmap, matrix, paint);
        imageView.setImageBitmap(modifiedBitmap);
    }

    private void changeImageBrightness(int brightness) {
        colorMatrix.set(new float[]{
                brightness/100.0f, 0, 0, 0, 0,
                0, brightness/100.0f, 0, 0, 0,
                0, 0, brightness/100.0f, 0, 0,
                0, 0, 0, 1, 0,
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(originalBitmap, matrix, paint);
        imageView.setImageBitmap(modifiedBitmap);
    }

    private void changeImageTemp(int temp) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
