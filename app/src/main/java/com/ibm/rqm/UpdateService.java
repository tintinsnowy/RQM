package com.ibm.rqm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
* 用于后台更新
* 具体的更新操作由AutoUpdater完成。
* */
public class UpdateService extends Service {
    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
