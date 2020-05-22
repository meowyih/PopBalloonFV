package tw.com.miifun.popballoonfv;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by yhorn on 2016/2/14.
 */
public class AppSetting {

    final static private String appTag = "AppSetting";

    static final String PREFS_NAME = "MIIFUN_BALLOON_SETTING";
    static final String PREFS_FULL_VERSION = "PREFS_FULL_VERSION";
    static final String PREFS_SPEED = "PREFS_SPEED";
    static final String PREFS_CRASHLOG = "PREF_CRASH_LOG";
    static final String PREFS_GAME_DATA = "PREFS_GAME_DATA";

    static final int VERSION_UNKNOWN = 0;
    static final int VERSION_TRIAL = 1;
    static final int VERSION_FULL = 2;

    static public boolean readBoolean( Application app, String hint ) {
        SharedPreferences settings = app.getSharedPreferences(AppSetting.PREFS_NAME, android.content.Context.MODE_PRIVATE);
        return settings.getBoolean(hint, false);
    }

    static public boolean writeBoolean( Application app, String hint, boolean status ) {
        SharedPreferences settings = app.getSharedPreferences( AppSetting.PREFS_NAME, android.content.Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(hint, status );
        return editor.commit();
    }

    static public String readString( Application app, String hint ) {
        SharedPreferences settings = app.getSharedPreferences(AppSetting.PREFS_NAME, android.content.Context.MODE_PRIVATE);
        return settings.getString(hint, null);
    }

    static public boolean writeString( Application app, String hint, String data ) {
        SharedPreferences settings = app.getSharedPreferences( AppSetting.PREFS_NAME, android.content.Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(hint, data);
        return editor.commit();
    }

    static public int readInt( Application app, String hint ) {
        SharedPreferences settings = app.getSharedPreferences(AppSetting.PREFS_NAME, android.content.Context.MODE_PRIVATE);
        return settings.getInt(hint, 0);
    }

    static public boolean writeInt( Application app, String hint, int status ) {
        SharedPreferences settings = app.getSharedPreferences( AppSetting.PREFS_NAME, android.content.Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(hint, status);
        return editor.commit();
    }

    static public boolean writeGameData( Application app, AppGameData data ) {

        Log.v( appTag, "writeGameData" );

        LinkedList<AppGameData.BalloonData> balloons = data.balloons;
        StringBuilder saveData = new StringBuilder();

        saveData.append( data.score ).append( ":" ).
                append(data.lastThumbScore).append( ":" ).
                append( data.lastSfxScore).append( ":" ).
                append(data.theme).append( ":" ).
                append(balloons.size());

        for ( int i = 0; i < balloons.size(); i ++ ) {
            AppGameData.BalloonData balloon = balloons.get(i);

            saveData.append(":");
            saveData.append(balloon.mItemType).append(":").
                    append(balloon.mState).append(":").
                    append(balloon.mSpeed).append(":").
                    append(balloon.mX).append(":").append(balloon.mY).append(":").
                    append(balloon.mWidth).append(":").append(balloon.mHeight);
        }

        SharedPreferences settings = app.getSharedPreferences( AppSetting.PREFS_NAME, android.content.Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREFS_GAME_DATA, saveData.toString());
        return editor.commit();
    }

    static public AppGameData readGameData( Application app ) {

        Log.v( appTag, "readGameData" );

        AppGameData data = new AppGameData();
        String saveData;
        int balloonsSize;
        int type, state, x, y, w, h;
        double speed;
        int anchor;

        SharedPreferences settings = app.getSharedPreferences(AppSetting.PREFS_NAME, android.content.Context.MODE_PRIVATE);
        saveData = settings.getString(PREFS_GAME_DATA, null);

        if ( saveData == null )
            return null;

        String[] fields = saveData.split(":");

        if ( fields.length < 5 ) {
            // broken save data
            Log.e(appTag, "err: broken save data, no header-" + saveData );
            return null;
        }

        try {
            data.score = Integer.parseInt(fields[0]);
            data.lastThumbScore = Integer.parseInt(fields[1]);
            data.lastSfxScore = Integer.parseInt(fields[2]);
            data.theme = Integer.parseInt(fields[3]);
            balloonsSize = Integer.parseInt(fields[4]);
        }
        catch( Exception e ) {
            Log.e( appTag, "err: bad header " + saveData );
            return null;
        }

        if ( fields.length != 5 + balloonsSize * 7 ) {
            Log.e( appTag, "err: wrong data size " + saveData );
            return null;
        }

        anchor = 5;

        try {
            for ( int i = 0; i < balloonsSize; i ++ ) {
                type = Integer.parseInt( fields[anchor++] );
                state = Integer.parseInt( fields[anchor++] );
                speed = Double.parseDouble(fields[anchor++] );
                x = Integer.parseInt( fields[anchor++] );
                y = Integer.parseInt( fields[anchor++] );
                w = Integer.parseInt( fields[anchor++] );
                h = Integer.parseInt( fields[anchor++] );
                data.addBalloonData( type, state, speed, x, y, w, h );
            }
        }
        catch ( Exception e ) {
            Log.e( appTag, "err: wrong balloon data " + saveData );
            return null;
        }

        return data;
    }
}
