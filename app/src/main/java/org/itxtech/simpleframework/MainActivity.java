package org.itxtech.simpleframework;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements Handler.Callback{
    public final static String name = "SimpleFramework";
    public final static int ACTION_STOP_SERVICE = 0;

    public static Handler actionHandler = null;
    private static Intent serverIntent = null;
    private static boolean isStarted = false;

    private Button buttonStart = null;
    private Button buttonStop = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionHandler = new Handler(this);

        serverIntent = new Intent(this, FrameworkService.class);

        FrameworkExecutor.appDirectory = getFilesDir().getParentFile();

        buttonStart = (Button) findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarted = true;
                startService(serverIntent);
                FrameworkExecutor.runFramework();
                refreshEnabled();
            }
        });
        buttonStop = (Button) findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStarted = false;
                FrameworkExecutor.kill();
                refreshEnabled();
            }
        });

        SeekBar seekBarFontSize = (SeekBar) findViewById(R.id.seekbar_fontsize);
        seekBarFontSize.setProgress(10);
        seekBarFontSize.setMax(30);
        seekBarFontSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar p1, int p2, boolean p3) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar p1) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar p1) {
                ConsoleActivity.fontSize = p1.getProgress();
            }
        });

        ConsoleActivity.fontSize = seekBarFontSize.getProgress();

        initAssets();
        refreshEnabled();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_console:
                startActivity(new Intent(this, ConsoleActivity.class));
        }
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case ACTION_STOP_SERVICE:
                isStarted = false;
                refreshEnabled();
                stopService(serverIntent);
                break;
        }
        return false;
    }

    public void refreshEnabled(){
        buttonStart.setEnabled(!isStarted);
        buttonStop.setEnabled(isStarted);
    }

    private void initAssets(){
        try {
            initAsset("busybox");
            initAsset("php");
        } catch (Exception ignored){
        }
    }

    private void initAsset(String fileName) throws Exception{
        File asset = new File( FrameworkExecutor.appDirectory + "/" + fileName);
        Log.d("GG", String.valueOf(asset.exists()));
        copyAsset(fileName, asset);
        asset.setExecutable(true, true);
        Log.d("GG", String.valueOf(asset.exists()));
    }

    private void copyAsset(String name, File target) throws Exception {
        target.delete();
        OutputStream os = new FileOutputStream(target);
        InputStream is = getAssets().open(name);
        int cou;
        byte[] buffer = new byte[8192];
        while ((cou = is.read(buffer)) != -1) {
            os.write(buffer, 0, cou);
        }
        is.close();
        os.close();
    }
}
