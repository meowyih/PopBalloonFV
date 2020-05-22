package tw.com.miifun.popballoonfv;

import android.app.Activity;
import android.app.Application;
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
public class ThreadGaming extends Thread implements EffectSoundPlayer.EffectSoundPlayerListener {

    final static private String appTag = "ThreadGaming";

    final static public String AUDIO_POP_1 = "pop-SodaBush-7991_hifi.mp3";
    final static public String AUDIO_POP_2 = "BubblePo-Benjamin-8920_hifi.mp3";
    final static public String AUDIO_POP_3 = "Pop-J_Fairba-8421_hifi.mp3";
    final static public String AUDIO_SUNNY_BGM = "BGM_Acoustic_Sunrise.mp3";
    final static public String AUDIO_NIGHT_BGM = "BGM_Rock_Guitar.mp3";
    final static public String AUDIO_RAINY_BGM = "Marimba_Melody_amp.mp3";
    final static public String AUDIO_RAINBOW_BGM = "BGM_Campfire.mp3";
    final static public String AUDIO_GRID_BGM = "BGM_4.mp3";
    final static public String AUDIO_DAY_BGM = "BGM_5.mp3";
    final static public String AUDIO_HELICOPTER = "helicopter.mp3";
    final static public String AUDIO_AIRPLANE = "Airplane_Taxi_short_amp.mp3";
    final static public String AUDIO_AIREPLANE_RUN_AWAY = "Flyby-Public_D-343_hifi_short.mp3";
    final static public String AUDIO_KID_GIGGLING = "Kids_Giggling_amp_short.mp3";
    final static public String AUDIO_WOFF = "Woff-Raphael-8706_hifi.mp3";
    final static public String AUDIO_RISER = "raiser_fx_amp.mp3";
    final static public String AUDIO_DOWNER = "downer_fx_amp_short.mp3";
    final static public String AUDIO_KID_CHEER = "Children_Cheering.mp3";
    final static public String AUDIO_BIRD_NORMAL = "Birds-Daniele_-7355_hifi_short.mp3";
    final static public String AUDIO_BIRD_CHU_CHU = "Chirp-Public_D-147_hifi_amp_long.mp3";
    final static public String AUDIO_WATER_DING_DONG = "bubles-dj_guy-c-7371_hifi.mp3";
    final static public String AUDIO_CARTON_BOING = "Cartoon_boing_amp.mp3";
    final static public String AUDIO_RAISER_WRRRRR = "Whrrrrrr-Intermed-569_hifi.mp3";

    final static int SFX_THUMB_DURATION = 2000;
    final static int SFX_THUMB_INTERVAL = 500;
    final static int SFX_SCORE_DURATION = 2000;
    final static int SFX_SPLASH_INTERVAL = 250;

    final static int MAX_THEME_DURATION = 3*60*1000; // max length of theme is 3 min

    private boolean mKeepRunning = true;
    private boolean mFinishActivityAtTheEnd = false;

    Context mContext;
    int mScreenWidth;
    int mScreenHeight;
    int mMaxBalloonWidth;
    int mMinBalloonWidth;
    SurfaceHolder mSurfaceHolder;
    MotionEvent mMotionEvent = null;

    // balloon list
    private LinkedList<ItemBalloon> mBalloons = new LinkedList<>();

    // sfx sound
    static private int mCounter;
    static private EffectSoundPlayer mAudioSfxFirst;
    static private EffectSoundPlayer mAudioSfxSecond;
    static private EffectSoundPlayer mAudioSfxLong;

    // bgm
    static EffectSoundPlayer mAudioBgm;

    // last balloon creation time
    long mLastBalloonCreationTime = 0;

    // last score trigger the firework
    int mLastThumbScore = 0;

    // last balloon exploded time
    long mLastBalloonExplodedTime = 0;

    // theme
    boolean mChangeThemeFlag = false;
    Theme mCurrentTheme, mThemeTmp;
    long mThemeChangeStartingTime = 0;
    ThemeForeground mThemeForeground;
    ThemeFrontSfx mThemeFrontSfx;
    ThemeBackSfx mThemeBackSfx;

    // score and speed
    int mScore, mSpeedLevel;

    // sfx
    int mLastSfxScore = 0;

    // save data
    boolean mReloadData;

    public ThreadGaming( Context context, SurfaceHolder holder, int width, int height, boolean reloadData ) {

        mContext = context;
        mScreenWidth = width;
        mScreenHeight = height;
        mSurfaceHolder = holder;
        mReloadData = reloadData;

        mMinBalloonWidth = context.getResources().getDimensionPixelSize( R.dimen.balloon_min_width );
        mMaxBalloonWidth = context.getResources().getDimensionPixelSize( R.dimen.balloon_max_width );

        if ( mAudioSfxFirst != null ) {
            mAudioSfxFirst.destroy();
            Log.e( appTag, "ERROR! mAudiorSfxFirst is not null. ONE GAME SHOULD HAVE ONLY ONE ThreadGaming! " );
        }

        if ( mAudioSfxSecond != null ) {
            mAudioSfxSecond.destroy();
            Log.e( appTag, "ERROR! mAudiorSfxSecond is not null, ONE GAME SHOULD HAVE ONLY ONE ThreadGaming! " );
        }

        if ( mAudioSfxLong != null ) {
            mAudioSfxLong.destroy();
            Log.e( appTag, "ERROR! mAudioSfxLong is not null, ONE GAME SHOULD HAVE ONLY ONE ThreadGaming! " );
        }

        if ( mAudioBgm != null ) {
            mAudioBgm.destroy();
            Log.e( appTag, "ERROR! mAudioBgm is not null. ONE GAME SHOULD HAVE ONLY ONE ThreadGaming! " );
        }

        mCounter = 0;
        mAudioSfxFirst = new EffectSoundPlayer( context );
        mAudioSfxSecond = new EffectSoundPlayer( context );
        mAudioSfxLong = new EffectSoundPlayer( context );
        mAudioBgm = new EffectSoundPlayer( context );
        mAudioBgm.setListsner( this );

        mScore = 0;

        // default theme is sunny
        mChangeThemeFlag = false;
        mCurrentTheme = new ThemeSunny( mContext, mScreenWidth, mScreenHeight );
        // mCurrentTheme = new ThemeRainy( mContext, mScreenWidth, mScreenHeight );
        // mCurrentTheme = new ThemeNight( mContext, mScreenWidth, mScreenHeight );
        // mCurrentTheme = new ThemeGrid( mContext, mScreenWidth, mScreenHeight );
        // mCurrentTheme = new ThemeDay( mContext, mScreenWidth, mScreenHeight );
        // mCurrentTheme = new ThemeRainbow( mContext, mScreenWidth, mScreenHeight );

        mThemeForeground = new ThemeForeground( mContext, mScreenWidth, mScreenHeight );

        mSpeedLevel = AppSetting.readInt(((Activity) mContext).getApplication(), AppSetting.PREFS_SPEED);

        if ( mSpeedLevel > 0 )
            mThemeForeground.setSpeed( mSpeedLevel );
        else
            mSpeedLevel = mThemeForeground.getSpeedLevel();
    }

    // warning! this object is NOT reusable
    public void stopThread() {
        mKeepRunning = false;
        interrupt();
    }

    public void stopThread( boolean finishActivity ) {
        if ( finishActivity ) {
            mFinishActivityAtTheEnd = true;
        }

        stopThread();
    }

    public void saveData() {
        AppGameData data = new AppGameData();
        data.score = mScore;
        data.theme = mCurrentTheme.id();
        data.lastThumbScore = mLastThumbScore;
        data.lastSfxScore = mLastSfxScore;
        data.addBalloonData( mBalloons );
        AppSetting.writeGameData( ((Activity)mContext).getApplication(), data );
    }

    static public void playAudioSfx( String audio ) {

        if ( ++mCounter % 2 == 0)
            mAudioSfxFirst.playEffectSound( EffectSoundPlayer.TYPE_ASSET, audio );
        else
            mAudioSfxSecond.playEffectSound( EffectSoundPlayer.TYPE_ASSET, audio );
    }

    @Override public void run() {

        Log.v( appTag, "enter game thread, mReloadData:" + mReloadData );

        ArrayList<DrawableItemEvent> eventList = new ArrayList<>();
        ArrayList<DrawableItem> trashCan = new ArrayList<>();
        DrawableItemEvent event;
        long time;
        boolean scoreChanged = false;
        ItemBalloon balloon;

        if ( mReloadData ) {
            AppGameData data = AppSetting.readGameData( ((Activity)mContext).getApplication() );
            if ( data != null ) {
                mScore = data.score;
                mLastThumbScore = data.lastThumbScore;
                mLastSfxScore = data.lastSfxScore;
                mCurrentTheme.destroy();
                switch( data.theme ) {
                    case Theme.THEME_SUNNY:
                        mCurrentTheme = new ThemeSunny( mContext, mScreenWidth, mScreenHeight );
                        break;
                    case Theme.THEME_NIGHT:
                        mCurrentTheme = new ThemeNight( mContext, mScreenWidth, mScreenHeight );
                        break;
                    case Theme.THEME_RAINY:
                        mCurrentTheme = new ThemeRainy( mContext, mScreenWidth, mScreenHeight );
                        break;
                    case Theme.THEME_RAINBOW:
                        mCurrentTheme = new ThemeRainbow( mContext, mScreenWidth, mScreenHeight );
                        break;
                    case Theme.THEME_GRID:
                        mCurrentTheme = new ThemeGrid( mContext, mScreenWidth, mScreenHeight );
                        break;
                    case Theme.THEME_DAY:
                        mCurrentTheme = new ThemeDay( mContext, mScreenWidth, mScreenHeight );
                        break;
                    default:
                        mCurrentTheme = new ThemeSunny( mContext, mScreenWidth, mScreenHeight );
                }

                for ( int i = 0; i < data.balloons.size(); i ++ ) {

                    AppGameData.BalloonData balloonData = data.balloons.get(i);

                    // we only need alive balloon
                    if ( balloonData.mState != ItemBalloon.RAISING )
                        continue;

                    mBalloons.add(new ItemBalloon(mContext, data.balloons.get(i), mScreenWidth, mScreenHeight));
                }
            }

            mThemeForeground.setScore( mScore );
        }

        time = System.currentTimeMillis();
        mCurrentTheme.skipThemeIn( time );
        // mCurrentTheme.startThemeIn( System.currentTimeMillis() );

        mThemeChangeStartingTime = time;
        mAudioBgm.playEffectSound(EffectSoundPlayer.TYPE_ASSET, getAudioByTheme( mCurrentTheme.id() ));


        while( mKeepRunning ) {

            scoreChanged = false;
            time = System.currentTimeMillis();

            // normally the theme changing is triggered by BGM complete listsner,
            // this is for safety if device audio player goes crazy
            if ( time - mThemeChangeStartingTime > MAX_THEME_DURATION ) {
                changeTheme();
            }

            // check if need to change theme
            if ( mChangeThemeFlag ) {
                Theme theme = mCurrentTheme;
                mCurrentTheme = mThemeTmp;
                mCurrentTheme.startThemeIn( time );
                mThemeTmp = theme;
                mThemeTmp.startThemeOut( time );
                mChangeThemeFlag = false;
                mAudioBgm.playEffectSound(EffectSoundPlayer.TYPE_ASSET, getAudioByTheme( mCurrentTheme.id() ));
            }

            mCurrentTheme.move(time);

            if ( mThemeTmp != null ) {
                if ( time - mThemeChangeStartingTime > mThemeTmp.durationOut() ) {
                    mThemeTmp.destroy();
                    mThemeTmp = null;
                }
                else {
                    mThemeTmp.move(time);
                }
            }

            balloon = getBalloon( time, mSpeedLevel );

            if ( balloon != null ) {
                mBalloons.add(balloon);
                // Log.v( appTag, "add new balloon into mBalloons, total " + mBalloons.size() + " balloons" );
            }

            // release sfx theme is no longer exist
            if ( mThemeFrontSfx != null && ! mThemeFrontSfx.mIsAlive ) {
                mThemeFrontSfx.destroy();
                mThemeFrontSfx = null;
                Log.v( appTag, "destroy mThemeFrontSfx since it is not alive anymore" );
            }

            if ( mThemeBackSfx != null && ! mThemeBackSfx.mIsAlive ) {
                mThemeBackSfx.destroy();
                mThemeBackSfx = null;
                Log.v( appTag, "destroy mThemeBackSfx since it is not alive anymore" );
            }

            Canvas canvas = mSurfaceHolder.lockCanvas();

            if ( canvas == null ) {
                Log.w( appTag, "mSurfaceHolder.lockCanvas() return null" );
                continue;
            }

            // clear canvas
            canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);

            // draw background
            mCurrentTheme.drawTheme(canvas);
            if ( mThemeTmp != null )
                mThemeTmp.drawTheme( canvas );

            // draw sfx (at back of the balloon)
            if ( mThemeBackSfx != null )
                mThemeBackSfx.drawTheme( canvas );

            // update all items and check if still alive
            for ( int i = 0; i < mBalloons.size(); i ++ ) {
                balloon =  mBalloons.get(i);

                // pass DrawableItem.Event
                for ( int j = 0; j < eventList.size(); j ++) {
                    balloon.onDrawableItemEvent( eventList.get(j) );
                }
                eventList.clear();

                // pass the motion event
                if ( mMotionEvent != null ) {
                    int ret;
                    ret = balloon.onTouchEvent(mMotionEvent);
                    if ( ret == ItemBalloon.BALLOON_CONSUMED_TOUCH_EVENT ) {
                        mMotionEvent = null;
                    }
                }

                // update the item
                event = balloon.move( time );

                // add event to event list if not DEAD or NONE
                if ( event.type != DrawableItemEvent.DEAD &&
                        event.type != DrawableItemEvent.DEAD_EXPLODED &&
                        event.type != DrawableItemEvent.NONE ) {
                    eventList.add( event );
                }

                // delete the dead object, if item alive
                if ( event.type == DrawableItemEvent.DEAD ||
                        event.type == DrawableItemEvent.DEAD_EXPLODED ) {
                    trashCan.add(balloon);
                }
                else {
                    balloon.draw( canvas );
                }

                // change score if exploded
                if ( event.type == DrawableItemEvent.DEAD_EXPLODED ) {
                    mScore ++;
                    scoreChanged = true;
                    mLastBalloonExplodedTime = time;
                    mThemeForeground.setScore( mScore );
                }
            }

            // check if need to show sfx
            if ( scoreChanged ) {
                determineSfxState(time);
            }

            // draw sfx (in front of the balloon)
            if ( mThemeFrontSfx != null )
                mThemeFrontSfx.drawTheme( canvas );

            // pass the motion event
            if ( mMotionEvent != null ) {
                mCurrentTheme.onTouchEvent(mMotionEvent);

                if ( mThemeTmp != null )
                    mThemeTmp.onTouchEvent(mMotionEvent);

                mThemeForeground.onTouchEvent( mMotionEvent );
            }

            mMotionEvent = null;

            // move sfx
            if ( mThemeFrontSfx != null ) {
                mThemeFrontSfx.move(time);
            }

            if ( mThemeBackSfx != null ) {
                mThemeBackSfx.move(time);
            }

            // move fore ground
            event = mThemeForeground.move( time );

            if ( event.type == DrawableItemEvent.EXIT_APPLICATION ) {
                // quite the loop and stop the application
                mKeepRunning = false;
                mFinishActivityAtTheEnd = true;
            }
            else if ( event.type == DrawableItemEvent.SPEED_UP ||
                    event.type == DrawableItemEvent.SPEED_DOWN ) {
                // change balloon speed
                mSpeedLevel = mThemeForeground.getSpeedLevel();
                AppSetting.writeInt( ((Activity)mContext).getApplication(), AppSetting.PREFS_SPEED, mSpeedLevel );
            }

            mThemeForeground.drawTheme( canvas );

            // update the screen
            mSurfaceHolder.unlockCanvasAndPost(canvas);

            // clear trash can
            for ( int i = 0; i < trashCan.size(); i ++) {
                DrawableItem item =  trashCan.get(i);
                mBalloons.remove( item);
                item.destroy();
            }
            trashCan.clear();
        }

        // release balloon module
        Log.d(appTag, "before finalize, mBitmapRecycleCount " + ItemBalloon.mBitmapRecycleCount );

        mThemeForeground.destroy();

        // destroy all items
        for ( int i = 0; i < mBalloons.size(); i ++ ) {
            mBalloons.get(i).destroy();
        }
        mBalloons.clear();
        mCurrentTheme.destroy();

        if ( mThemeTmp != null )
            mThemeTmp.destroy();
        mThemeTmp = null;

        if ( mThemeBackSfx != null )
            mThemeBackSfx.destroy();
        mThemeBackSfx = null;

        if ( mThemeFrontSfx != null )
            mThemeFrontSfx.destroy();
        mThemeFrontSfx = null;

        BitmapWarehouse.release();

        if ( mAudioSfxFirst != null ) {
            mAudioSfxFirst.destroy();
            mAudioSfxFirst = null;
        }

        if ( mAudioSfxSecond != null ) {
            mAudioSfxSecond.destroy();
            mAudioSfxSecond = null;
        }

        if ( mAudioSfxLong != null ) {
            mAudioSfxLong.destroy();
            mAudioSfxLong = null;
        }

        if ( mAudioBgm != null ) {
            mAudioBgm.destroy();
            mAudioBgm = null;
        }

        // Log.d(appTag, "after finalize, mBitmapRecycleCount " + ItemBalloon.mBitmapRecycleCount);
        // Log.v( appTag, "Correctly exist game thread" );

        if ( mFinishActivityAtTheEnd ) {
            ((Activity)mContext).finish();
        }
    }

    public boolean onTouchEvent( MotionEvent event ) {
        // actually not a good way to handle it, for example, if the onTouchEvent process speed
        // is faster than the screen refresh, it is possible that some MotionEvent be ignored.
        // i.e. twice onTouchEvent passed in one thread while-loop process.
        // But, hey, if such thing happens, no end-user will tolerant such low frame refreshing rate.
        mMotionEvent = event;
        return true;
    }

    // EffectSoundPlayer.EffectSoundPlayerListener
    public void onEffectSoundFinish( EffectSoundPlayer mp, int result ) {

        if ( mp != null && mp == mAudioBgm ) {
            Log.v( appTag, "BGM is done, change theme." );
            changeTheme();
        }

        return;
    }

    void changeTheme() {

        if (mCurrentTheme.id() == Theme.THEME_SUNNY)
        {
            mThemeTmp = new ThemeRainy( mContext, mScreenWidth, mScreenHeight );
            mChangeThemeFlag = true;
            mThemeChangeStartingTime = System.currentTimeMillis();
        }
        else if (mCurrentTheme.id() == Theme.THEME_RAINY)
        {
            mThemeTmp = new ThemeRainbow( mContext, mScreenWidth, mScreenHeight );
            mChangeThemeFlag = true;
            mThemeChangeStartingTime = System.currentTimeMillis();
        }
        else if (mCurrentTheme.id() == Theme.THEME_RAINBOW)
        {
            mThemeTmp = new ThemeNight( mContext, mScreenWidth, mScreenHeight );
            mChangeThemeFlag = true;
            mThemeChangeStartingTime = System.currentTimeMillis();
        }
        else if (mCurrentTheme.id() == Theme.THEME_NIGHT)
        {
            mThemeTmp = new ThemeDay( mContext, mScreenWidth, mScreenHeight );
            mChangeThemeFlag = true;
            mThemeChangeStartingTime = System.currentTimeMillis();
        }
        else if (mCurrentTheme.id() == Theme.THEME_DAY)
        {
            mThemeTmp = new ThemeGrid( mContext, mScreenWidth, mScreenHeight );
            mChangeThemeFlag = true;
            mThemeChangeStartingTime = System.currentTimeMillis();
        }
        else if (mCurrentTheme.id() == Theme.THEME_GRID)
        {
            mThemeTmp = new ThemeSunny( mContext, mScreenWidth, mScreenHeight );
            mChangeThemeFlag = true;
            mThemeChangeStartingTime = System.currentTimeMillis();
        }
    }

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
        type = ItemBalloon.getRandomType();
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

    void determineSfxState( long time ) {
        boolean showCenterScore = false;
        boolean showThumb = false;
        boolean showFirework = false;

        if ( mScore >= 30 && mLastSfxScore == 0 ) {
            showCenterScore = true;
            mLastSfxScore = 30;
        }
        else if ( mScore >= 50 && mLastSfxScore == 30 ) {
            showCenterScore = true;
            mLastSfxScore = 50;
        }
        else if ( mScore >= 100 && mLastSfxScore == 50 ) {
            showCenterScore = true;
            mLastSfxScore = 90;
        }
        else if ( mScore >= 200 && mLastSfxScore == 100 ) {
            showCenterScore = true;
            mLastSfxScore = 200;
        }
        else if ( mScore >= 400 && mLastSfxScore == 200 ) {
            showCenterScore = true;
            mLastSfxScore = 400;
        }
        else if ( mScore >= 700 && mLastSfxScore == 400 ) {
            showCenterScore = true;
            mLastSfxScore = 700;
        }
        else if ( mScore >= 999 && mLastSfxScore == 700 ) {
            showCenterScore = true;
            mLastSfxScore = 999;
        }
        else if ( mScore - mLastThumbScore >= 10 ) {
            // showFirework = true;
            showThumb = true;
            mLastThumbScore = mScore;
        }

        // audio sfx
        if ( showCenterScore || showThumb ) {
            mAudioSfxLong.playEffectSound( EffectSoundPlayer.TYPE_ASSET, AUDIO_KID_CHEER );
        }

        // front sfx
        if ( showCenterScore || showFirework || showThumb ) {
            if ( mThemeFrontSfx == null ) {
                Log.v( appTag, "create mThemeFrontSfx");
                mThemeFrontSfx = new ThemeFrontSfx(mContext, mScreenWidth, mScreenHeight);
            }

            if ( showCenterScore ) {
                mThemeFrontSfx.showNumber(mScore, time, time + SFX_SCORE_DURATION);
            }

            if ( showThumb ) {
                mThemeFrontSfx.showThumb( time, time +SFX_THUMB_DURATION, SFX_THUMB_INTERVAL );
            }
        }

        // back sfx
        if ( showCenterScore ) {
            if ( mThemeBackSfx == null ) {
                mThemeBackSfx = new ThemeBackSfx(mContext, mScreenWidth, mScreenHeight);
            }

            mThemeBackSfx.showSplash( time, time + SFX_SCORE_DURATION, SFX_SPLASH_INTERVAL );
        }
    }

    static private String getAudioByTheme( int theme ) {
        switch( theme ) {
            case Theme.THEME_SUNNY:
                return AUDIO_SUNNY_BGM;
            case Theme.THEME_NIGHT:
                return AUDIO_NIGHT_BGM;
            case Theme.THEME_RAINY:
                return AUDIO_RAINY_BGM;
            case Theme.THEME_RAINBOW:
                return AUDIO_RAINBOW_BGM;
            case Theme.THEME_GRID:
                return AUDIO_GRID_BGM;
            case Theme.THEME_DAY:
                return AUDIO_DAY_BGM;
            default:
                return AUDIO_SUNNY_BGM;
        }
    }
}
