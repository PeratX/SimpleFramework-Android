package org.itxtech.simpleframework;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

/**
 * SimpleFramework Project
 *
 * @author PeratX
 * @link https://itxtech.org
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
public class FrameworkService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, new NotificationCompat.Builder(getApplicationContext()).setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(getString(R.string.message_tap_open))
                .setContentTitle(MainActivity.name + " " + getString(R.string.message_running))
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(getApplicationContext(), MainActivity.class), 0))
                .build());
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        FrameworkExecutor.kill();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
