package tw.com.miifun.popballoonfv;

import android.app.Application;
import android.util.Log;

/**
 * Created by yhorn on 2016/3/12.
 */
public class MyApplication extends Application {

    final static private String appTag = "MyApplication";

    Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    public void onCreate() {

        super.onCreate();

        // Setup handler for uncaught exceptions.
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                Log.e(appTag, "enter custom setDefaultUncaughtExceptionHandler");
                handleUncaughtException(thread, e);
            }
        });
    }

    public void handleUncaughtException (Thread thread, Throwable e) {
        // e.printStackTrace(); // not all Android versions will print the stack trace automatically
        // write crash log
        AppSetting.writeString(this, AppSetting.PREFS_CRASHLOG, Log.getStackTraceString(e) );

        // start crash dialog
        /*
        Intent intent = new Intent();
        intent.setAction ("tw.com.miifun.miifunplayer.ACL");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
        intent.putExtra( AclActivity.CRASHLOG, Log.getStackTraceString(e) );
        startActivity(intent);
        */
        mDefaultUncaughtExceptionHandler.uncaughtException( thread, e );
    }
}
