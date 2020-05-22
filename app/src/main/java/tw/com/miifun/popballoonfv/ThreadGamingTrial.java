package tw.com.miifun.popballoonfv;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by yhorn on 2016/2/18.
 */
public class ThreadGamingTrial extends ThreadGaming {

    final static private String appTag = "ThreadGamingTrial";

    public ThreadGamingTrial(Context context, SurfaceHolder holder, int width, int height, boolean reloadData ) {
        super(context, holder, width, height, reloadData);

        mThemeForeground.setMaxSpeedLevel( 5 ); // only 5 level for trial
    }

    // EffectSoundPlayer.EffectSoundPlayerListener
    @Override
    public void onEffectSoundFinish( EffectSoundPlayer mp, int result ) {

        if ( mp != null && mp == mAudioBgm ) {
            if (mCurrentTheme.id() == Theme.THEME_SUNNY)
            {
                mThemeTmp = new ThemeRainy( mContext, mScreenWidth, mScreenHeight );
                mChangeThemeFlag = true;
                mThemeChangeStartingTime = System.currentTimeMillis();
            }
            else if (mCurrentTheme.id() == Theme.THEME_RAINY )
            {
                mThemeTmp = new ThemeNight( mContext, mScreenWidth, mScreenHeight );
                mChangeThemeFlag = true;
                mThemeChangeStartingTime = System.currentTimeMillis();
            }
            else if (mCurrentTheme.id() == Theme.THEME_NIGHT)
            {
                mThemeTmp = new ThemeSunny( mContext, mScreenWidth, mScreenHeight );
                mChangeThemeFlag = true;
                mThemeChangeStartingTime = System.currentTimeMillis();
            }
        }

        return;
    }

    @Override
    ItemBalloon getBalloon( long time, int level ) {

        boolean createBalloon = false;
        int width;
        int height;
        int type;
        int x, y;
        int speedDistanceLower, speedDistanceUpper; // mm per second
        double speed;
        long balloonBornInterval;
        long interval;

        switch ( level ) {
            case 0: balloonBornInterval = 7000; speedDistanceLower = 5; speedDistanceUpper = 10; break;
            case 1: balloonBornInterval = 6000; speedDistanceLower = 5; speedDistanceUpper = 10; break;
            case 2: balloonBornInterval = 5000; speedDistanceLower = 5; speedDistanceUpper = 10; break;
            case 3: balloonBornInterval = 4000; speedDistanceLower = 5; speedDistanceUpper = 10; break;
            case 4: balloonBornInterval = 3000; speedDistanceLower = 5; speedDistanceUpper = 15; break;
            case 5: balloonBornInterval = 2000; speedDistanceLower = 5; speedDistanceUpper = 15; break;
            case 6: balloonBornInterval = 1000; speedDistanceLower = 5; speedDistanceUpper = 15; break;
            case 7: balloonBornInterval = 600; speedDistanceLower = 5; speedDistanceUpper = 15; break;
            case 8: balloonBornInterval = 300; speedDistanceLower = 5; speedDistanceUpper = 20; break;
            case 9: balloonBornInterval = 100; speedDistanceLower = 5; speedDistanceUpper = 20; break;
            default: balloonBornInterval = 5000; speedDistanceLower = 5; speedDistanceUpper = 10;
        }

        if ( mLastBalloonCreationTime == 0 ) {
            // create first balloon!
            createBalloon = true;
        }
        else {
            interval = time - mLastBalloonCreationTime;

            // example, if we want to create balloon every 5 sec
            if ( interval > balloonBornInterval ) {
                createBalloon = true;
            }
        }

        if ( ! createBalloon )
            return null;

        // Log.v( appTag, "create balloon time:" + time + " last:" + mLastBalloonCreationTime + " interval" + interval + " balloonBornInterval:" + balloonBornInterval);

        mLastBalloonCreationTime = time;

        // determine the balloon size
        type = ItemBalloon.getRandomTypeForTrial();
        width = mMinBalloonWidth + (int)( Math.random() * ( mMaxBalloonWidth - mMinBalloonWidth ));
        height = ItemBalloon.getHeightByWidth( mContext, type, width );

        if ( level <= 3 ) {
            x = mScreenWidth / 4 + (int) (Math.random() * (mScreenWidth/2));
        }
        else {
            x = width / 2 + (int) (Math.random() * (mScreenWidth - width));
        }
        y = mScreenHeight;
        // mm per 1 ms
        speed = DrawUtil.dpToPx(
                DrawUtil.mmToDp(
                        (int)(Math.random() * speedDistanceLower ) + ( speedDistanceUpper - speedDistanceLower ) ));

        // Log.v( appTag, "balloon x:" + x + " y:" + y + " w:" + width + " h:" + height + " speed:" + speed );

        return new ItemBalloon(
                mContext,
                type,
                x, y,
                width, height,
                mScreenWidth, mScreenHeight, speed );

    }
}
