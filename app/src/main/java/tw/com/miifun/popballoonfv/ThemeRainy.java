package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.LinkedList;

import androidx.core.content.ContextCompat;

/**
 * Created by yhorn on 2016/2/21.
 */
public class ThemeRainy extends Theme {

    final static private String appTag = "ThemeRainy";

    static private int mBitmapRecycleCounter = 0;

    final static long SLOW_RAIN_DROP_INTERVAL = 3000; // 1000 ms per drop
    final static long FAST_RAIN_DROP_INTERVAL = 500; // 500 ms per drop
    final static long RAIN_DROP_LIVING_TIME = 6000; // 2 sec to reach the ground

    final static int CLOUD_NEED_DROP_MORE_RAIN = 1;

    private long DURATION_IN = 5000;
    private long DURATION_OUT = 5000;

    long mLastRainDropTime;
    int mRainWidth;
    int mRainHeight;

    long mThemeInTime, mThemeOutTime;

    ArrayList<ItemData> mClouds = new ArrayList<>();
    LinkedList<ItemRainDrop> mRainDrops = new LinkedList<>();

    public ThemeRainy( Context context, int screenWidth, int screenHeight ) {

        super( context, screenWidth, screenHeight );

        // create cloud group
        ItemData cloud;

        // cloud#1
        cloud = new ItemData();
        cloud.x = mScreenWidth / 50;
        cloud.y = mScreenHeight * 2 / 15;
        cloud.w = mScreenWidth * 3 / 7;
        cloud.h = cloud.w * context.getResources().getInteger( R.integer.cloud_rainy_white_height ) /
                context.getResources().getInteger( R.integer.cloud_rainy_white_width );
        cloud.xIn = 0 - cloud.w;
        cloud.yIn = cloud.y;
        cloud.xOut = cloud.xIn;
        cloud.yOut = cloud.yIn;

        if ( cloud.createItem( Theme.ItemData.RAINY_CLOUD ) != null ) {

            cloud.item.setBitmap( R.drawable.cloud_rainy_white);

            if ( cloud.item.mBitmap != null ) {
                mBitmapRecycleCounter++;
                mClouds.add( cloud );
            }
        }

        // cloud#2
        cloud = new ItemData();
        cloud.x = mScreenWidth * 4 / 50;
        cloud.y = mScreenHeight * 1 / 40;
        cloud.w = mScreenWidth * 3 / 7;
        cloud.h = cloud.w * context.getResources().getInteger( R.integer.cloud_rainy_grey_height ) /
                context.getResources().getInteger( R.integer.cloud_rainy_grey_width );

        cloud.xIn = 0 - cloud.w; // we know cloud width is large
        cloud.yIn = 0 - cloud.w;
        cloud.xOut = cloud.xIn;
        cloud.yOut = cloud.yIn;

        if ( cloud.createItem( Theme.ItemData.RAINY_CLOUD ) != null ) {

            cloud.item.setBitmap( R.drawable.cloud_rainy_grey);

            if ( cloud.item.mBitmap != null ) {
                mBitmapRecycleCounter++;
                mClouds.add( cloud );
            }
        }

        // cloud#3
        cloud = new ItemData();
        cloud.x = mScreenWidth * 2 / 5;
        cloud.y = mScreenHeight * 4 / 40;
        cloud.w = mScreenWidth * 3 / 7;
        cloud.h = cloud.w * context.getResources().getInteger( R.integer.cloud_rainy_grey_height ) /
                context.getResources().getInteger( R.integer.cloud_rainy_grey_width );
        cloud.xIn = 0 + cloud.w;
        cloud.yIn = 0 - cloud.w;
        cloud.xOut = cloud.xIn;
        cloud.yOut = cloud.yIn;

        if ( cloud.createItem( Theme.ItemData.RAINY_CLOUD ) != null ) {

            cloud.item.setBitmap( R.drawable.cloud_rainy_grey);

            if ( cloud.item.mBitmap != null ) {
                mBitmapRecycleCounter++;
                mClouds.add( cloud );
            }
        }

        // cloud#4
        cloud = new ItemData();
        cloud.x = mScreenWidth * 2 / 3;
        cloud.y = mScreenHeight * 8 / 40;
        cloud.w = mScreenWidth * 3 / 10;
        cloud.h = cloud.w * context.getResources().getInteger( R.integer.cloud_rainy_white_height ) /
                context.getResources().getInteger( R.integer.cloud_rainy_white_width );
        cloud.xIn = mScreenWidth;
        cloud.yIn = cloud.y;
        cloud.xOut = cloud.xIn;
        cloud.yOut = cloud.yIn;

        if ( cloud.createItem( ItemData.RAINY_CLOUD ) != null ) {

            cloud.item.setBitmap( R.drawable.cloud_rainy_white);

            if ( cloud.item.mBitmap != null ) {
                mBitmapRecycleCounter++;
                mClouds.add( cloud );
            }
        }

        // rain drop
        mRainWidth = mScreenWidth * 1 / 10;
        mRainHeight = mRainWidth * context.getResources().getInteger( R.integer.cloud_rainy_drop_height ) /
                context.getResources().getInteger( R.integer.cloud_rainy_drop_width );
        mLastRainDropTime = System.currentTimeMillis();
    }

    @Override
    public int id() { return THEME_RAINY; }

    @Override
    public long durationIn() {
        return DURATION_IN;
    }

    @Override
    public long durationOut() {
        return DURATION_OUT;
    }

    @Override
    public void skipThemeIn( long time ) {
        mState = Theme.STATE_RUN;

        for ( int i = 0; i < mClouds.size(); i ++ ) {
            ItemData data = mClouds.get(i);
            data.item.setDestination( data.x, data.y, time, 0 );
        }
    }

    @Override
    public void startThemeIn( long time ) {
        mState = Theme.STATE_IN;
        mThemeInTime = time;

        for ( int i = 0; i < mClouds.size(); i ++ ) {
            ItemData data = mClouds.get(i);
            data.item.setDestination( data.x, data.y, time, durationIn() );
        }
    }

    @Override
    public void startThemeOut( long time ) {
        mState = Theme.STATE_OUT;
        mThemeOutTime = time;

        for ( int i = 0; i < mClouds.size(); i ++ ) {
            ItemData data = mClouds.get(i);
            data.item.setDestination(data.xOut, data.yOut, time, durationOut());
        }
    }

    @Override
    public boolean drawTheme( Canvas canvas ) {

        // draw background
        Paint paint = new Paint();
        Rect rect;

        if ( mState == STATE_IN ) {
            int y = (int)( mScreenHeight * ( System.currentTimeMillis() - mThemeInTime ) / durationIn());
            if ( y < 0 ) y = 0;
            if ( y > mScreenHeight ) y = mScreenHeight;
            rect = new Rect( 0, 0, mScreenWidth, y );
        }
        else if ( mState == STATE_OUT ) {
            int y = (int)( mScreenHeight * ( System.currentTimeMillis() - mThemeOutTime ) / durationOut());
            if ( y < 0 ) y = 0;
            if ( y > mScreenHeight ) y = mScreenHeight;
            rect = new Rect( 0, y, mScreenWidth, mScreenHeight );
        }
        else {
            rect = new Rect(0, 0, mScreenWidth, mScreenHeight);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(mContext, R.color.ThemeRainyBackground));
        canvas.drawRect(rect, paint);

        // draw item
        for ( int i = 0; i < mClouds.size(); i ++ ) {
            ItemData data = mClouds.get(i);
            data.item.draw(canvas);
        }

        for ( int i = 0; i < mRainDrops.size(); i ++ ) {
            ItemRainDrop drop = mRainDrops.get(i);
            drop.draw(canvas);
        }

        return true;
    }

    @Override
    public DrawableItemEvent move( long time ) {

        int x, y;

        if ( mState == STATE_IN ) {
            if ( time - mThemeInTime > durationIn() ) {
                mState = STATE_RUN;
            }
        }

        if ( mState == STATE_OUT ) {
            if ( time - mThemeOutTime > durationOut() ) {
                mState = STATE_RUN;
            }
        }

        if ( mState == STATE_RUN) {
            // determine if need to drop rain
            if ( time - mLastRainDropTime > SLOW_RAIN_DROP_INTERVAL ) {

                for ( int i = 0; i < mClouds.size(); i ++ ) {
                    x = mClouds.get(i).x + (int)(Math.random() * mClouds.get(i).w);
                    y = mClouds.get(i).y + (int)(Math.random() * mClouds.get(i).h);
                    ItemRainDrop rain = new ItemRainDrop( mContext, 0, x, y, mRainWidth, mRainHeight, mScreenWidth, mScreenHeight );
                    mBitmapRecycleCounter ++;
                    rain.setBitmap( R.drawable.rain_drop );
                    rain.setDestination(x - (mScreenHeight - y), mScreenHeight, time, RAIN_DROP_LIVING_TIME);
                    mRainDrops.add(rain);
                    // Log.v( appTag, "create slow rain drop" );
                }

                mLastRainDropTime = time;
            }
            else if ( time - mLastRainDropTime > FAST_RAIN_DROP_INTERVAL ) {

                for ( int i = 0; i < mClouds.size(); i ++ ) {

                    if ( mClouds.get(i).custom == CLOUD_NEED_DROP_MORE_RAIN ) {
                        x = mClouds.get(i).x + (int) (Math.random() * mClouds.get(i).w);
                        y = mClouds.get(i).y + (int) (Math.random() * mClouds.get(i).h);
                        ItemRainDrop rain = new ItemRainDrop(mContext, 0, x, y, mRainWidth, mRainHeight, mScreenWidth, mScreenHeight);
                        mBitmapRecycleCounter ++;
                        rain.setBitmap(R.drawable.rain_drop);
                        rain.setDestination(x - (mScreenHeight - y), mScreenHeight, time, RAIN_DROP_LIVING_TIME);
                        mRainDrops.add(rain);
                        mClouds.get(i).custom = 0;

                        // Log.v( appTag, "create fast rain drop under cloud " + i );
                    }
                }
            }
        }

        // move item
        for (int i = 0; i < mClouds.size(); i ++ ) {
            ItemData data = mClouds.get(i);
            data.item.move(time);
        }

        for ( int i = 0; i < mRainDrops.size(); i ++ ) {
            ItemRainDrop drop = mRainDrops.get(i);
            DrawableItemEvent event = drop.move( time );

            if ( event.type == DrawableItemEvent.DEAD ) {
                drop.destroy();
                mRainDrops.remove( drop );
                mBitmapRecycleCounter --;
                // Log.v( appTag, "destroy rain, alive rain " + mRainDrops.size() );
            }
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, 0, 0 );
    }

    @Override
    public void destroy() {
        for ( int i = 0; i < mClouds.size(); i ++ ) {
            ItemData data = mClouds.get(i);
            data.item.destroy();
            mBitmapRecycleCounter --;
        }

        for ( int i = 0; i < mRainDrops.size(); i ++ ) {
            ItemRainDrop drop = mRainDrops.get(i);
            drop.destroy();
            mBitmapRecycleCounter --;
        }

        Log.v(appTag, "mBitmapRecycleCounter " + mBitmapRecycleCounter );
    }

    // MotionEvent handler
    public void onTouchEvent( MotionEvent event ) {
        for ( int i = 0; i < mClouds.size(); i ++ ) {
            ItemRainyCloud cloud = (ItemRainyCloud)( mClouds.get(i).item );

            if ( cloud.isShaking() ) {
                continue;
            }
            if ( cloud.onTouchEvent( event ) == ItemRainyCloud.SHAKE ) {
                mClouds.get(i).custom = CLOUD_NEED_DROP_MORE_RAIN;
                break;
            }
        }
    }
}
