package com.drop.aidlapi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import id.co.vostra.vanguard.dpc.IDeviceService;

public class AidlAPI {
    public static final String PACKAGE_NAME = "id.co.vostra.vanguard.dpc";
    public static final String SERVICE_CLS = "id.co.vostra.vanguard.dpc.service.DeviceInfoService";

    public static boolean serviceBound = false;
    private IDeviceService iDeviceService;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iDeviceService = IDeviceService.Stub.asInterface(iBinder);
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;
        }
    };

    public void connect(Context context) {
        Intent bindIntent = new Intent();
        bindIntent.setComponent(new ComponentName(PACKAGE_NAME, SERVICE_CLS));
        context.bindService(bindIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public String getImei(Context context) {
        if (iDeviceService == null) {
            Toast.makeText(context, "aidl service not bind", Toast.LENGTH_SHORT).show();
            return null;
        }
        try {
            String imei = iDeviceService.getImei();
            Toast.makeText(context, imei, Toast.LENGTH_LONG).show();
            return imei;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void disconnect(Context context) {
        if (serviceBound && iDeviceService != null) {
            context.unbindService(serviceConnection);
            serviceBound = false;

            Intent intent = new Intent();
            intent.setComponent(new ComponentName(PACKAGE_NAME, SERVICE_CLS));
            context.stopService(intent);
        }

        iDeviceService = null;
    }

    public boolean isBinding() {
        return serviceBound && iDeviceService != null;
    }
}
