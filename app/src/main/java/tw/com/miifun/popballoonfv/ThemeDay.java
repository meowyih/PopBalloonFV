package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.LinkedList;

import androidx.core.content.ContextCompat;

/**
 * Created by yhorn on 2016/2/21.
 */
public class ThemeDay extends Theme {

    private long DURATION_IN = 5000;
    private long DURATION_OUT = 5000;
    final static int BIRD_FLIGHT_INTERVAL = 10000;
    final static int BIRD_CREATE_INTERVAL = 3000;
    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;

    long mThemeInTime, mThemeOutTime;

    LinkedList<ItemBird> mBirds = new LinkedList<>();
    int mBirdWidth, mBirdHeight;
    long mLastCreateBirdTime = 0;

    public ThemeDay( Context context, int screenWidth, int screenHeight ) {
        super( context, screenWidth, screenHeight );

        mContext = context;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;

        mBirdWidth = mScreenWidth / 5;
        mBirdHeight = mBirdWidth * mContext.getResources().getInteger( R.integer.bird_height )
                / mContext.getResources().getInteger( R.integer.aircab_width );
    }

    @Override
    public int id() { return THEME_DAY; }

    @Override
    public long durationIn() {
        return DURATION_IN;
    }

    @Override
    public long durationOut() {
        return DURATION_OUT;
    }

    @Override
    public void startThemeIn( long time ) {
        mState = Theme.STATE_IN;
        mThemeInTime = time;
    }

    @Override
    public void startThemeOut( long time ) {
        mState = Theme.STATE_OUT;
        mThemeOutTime = time;
    }

    @Override
    public void skipThemeIn( long time ) {
        mState = Theme.STATE_RUN;

        // mBird = new ItemBird( mContext, 0, mBirdinX, mBirdinY, mBirdWidth, mBirdHeight, mScreenWidth, mScreenHeight );
        // mBird.setDestination( mBirdoutX, mBirdoutY, time, BIRD_FLIGHT_INTERVAL );
    }

    @Override
    public DrawableItemEvent move( long time ) {

        if ( mState == Theme.STATE_IN && time - mThemeInTime > durationIn() ) {
            mState = Theme.STATE_RUN;

            // mBird = new ItemBird( mContext, 0, mBirdinX, mBirdinY, mBirdWidth, mBirdHeight, mScreenWidth, mScreenHeight );
            // mBird.setDestination( mBirdoutX, mBirdoutY, time, BIRD_FLIGHT_INTERVAL );
        }

        if ( mState == Theme.STATE_OUT && time - mThemeOutTime > durationOut() ) {
            mState = Theme.STATE_RUN;

            // mBird = new ItemBird( mContext, 0, mBirdinX, mBirdinY, mBirdWidth, mBirdHeight, mScreenWidth, mScreenHeight );
            // mBird.setDestination( mBirdoutX, mBirdoutY, time, BIRD_FLIGHT_INTERVAL );
        }

        // create bird
        if ( mState == Theme.STATE_RUN ) {
            if ( mLastCreateBirdTime == 0 || time - mLastCreateBirdTime >= BIRD_CREATE_INTERVAL ) {
                int random = (int)( Math.random() * 10 );
                int type = ( random > 4 ? ItemBird.LEFT_TO_RIGHT : ItemBird.RIGHT_TO_LEFT );
                int x = ( type ==  ItemBird.LEFT_TO_RIGHT ? 0 - mBirdWidth : mScreenWidth );
                int destX = ( type == ItemBird.LEFT_TO_RIGHT ? mScreenWidth : 0 - mBirdWidth );
                int gap = ( mScreenHeight - mBirdHeight * 2 ) / 4;
                int y = mBirdHeight + ( random % 5 ) * gap;
                ItemBird bird = new ItemBird( mContext, type, x, y, mBirdWidth, mBirdHeight, mScreenWidth, mScreenHeight );
                mBirds.add( bird );
                bird.setDestination( destX, y, time, BIRD_FLIGHT_INTERVAL );
                mLastCreateBirdTime = time;
            }
        }

        // move bird
        ArrayList<ItemBird> trashcan = new ArrayList<>();

        for ( int i = 0; i < mBirds.size(); i ++ ) {
            ItemBird bird = mBirds.get(i);
            DrawableItemEvent event = bird.move(time);
            if ( event.type == DrawableItemEvent.DEAD ) {
                trashcan.add( bird );
            }
        }

        for ( int i = 0; i < trashcan.size(); i ++ ) {
            ItemBird bird = trashcan.get(i);
            mBirds.remove(bird);
            bird.destroy();
        }

        trashcan.clear();

        return new DrawableItemEvent( DrawableItemEvent.NONE, 0, 0 );
    }

    @Override
    public boolean drawTheme( Canvas canvas ) {

        Shader shader = new LinearGradient(0, 0, 0, mScreenHeight,
                ContextCompat.getColor(mContext, R.color.ThemeDayStartBackground),
                ContextCompat.getColor(mContext, R.color.ThemeDayEndBackground),
                Shader.TileMode.CLAMP);

        Paint paint = new Paint();
        paint.setShader(shader);

        if ( mState == STATE_IN ) {
            int y = (int)( mScreenHeight * ( System.currentTimeMillis() - mThemeInTime ) / durationIn());
            if ( y < 0 ) y = 0;
            if ( y > mScreenHeight ) y = mScreenHeight;
            canvas.drawRect(new RectF(0, 0, mScreenWidth, y), paint);
        }
        else if ( mState == STATE_OUT ) {
            int y = (int)( mScreenHeight * ( System.currentTimeMillis() - mThemeOutTime ) / durationOut());
            if ( y < 0 ) y = 0;
            if ( y > mScreenHeight ) y = mScreenHeight;
            canvas.drawRect(new RectF(0, y, mScreenWidth, mScreenHeight), paint);
        }
        else {
            canvas.drawRect(new RectF(0, 0, mScreenWidth, mScreenHeight), paint);
        }

        for ( int i = 0; i < mBirds.size(); i ++ ) {
            ItemBird bird = mBirds.get(i);
            bird.draw( canvas );
        }

        return true;
    }

    @Override
    public void destroy() {
        for ( int i = 0; i < mBirds.size(); i ++ ) {
            ItemBird bird = mBirds.get(i);
            bird.destroy();
        }

        mBirds.clear();
    }

    @Override
    public void onTouchEvent( MotionEvent event ) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                for ( int i = 0; i < mBirds.size(); i ++ ) {
                    ItemBird bird = mBirds.get(i);
                    bird.onTouchEvent( event );
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
            default:
        }
    }
}
