package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/22.
 */
public class ItemFireBalloon extends ItemMoving {

    final static String appTag = "ItemFireBalloon";

    final static int RUN_AWAY_DURATION = 1000;
    final static public int TYPE_1 = 1;
    final static public int TYPE_2 = 2;
    final static public int TYPE_3 = 3;
    final static public int TYPE_RANDOM = 100;

    private boolean mIsRunningAway = false;


    public ItemFireBalloon(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);

        if ( type == TYPE_RANDOM ) {
            int random = (int)(Math.random() * 3) + 1;
            if ( random == 1 )
                type = TYPE_1;
            else if ( random == 2 )
                type = TYPE_2;
            else
                type = TYPE_3;
        }

        if ( type == TYPE_1 ) {
            setBitmap(R.drawable.fire_balloon_1);
        }
        else if ( type == TYPE_2 ) {
            setBitmap(R.drawable.fire_balloon_2);
        }
        else if ( type == TYPE_3 ) {
            setBitmap(R.drawable.fire_balloon_3);
        }
        else {
            Log.e(appTag, "ERROR: ItemFireBalloon MUST assign a meaningful type to create image");
        }


    }

    @Override
    DrawableItemEvent move(long time) {
        super.move(time);

        if ( mX == mDestX && mY == mDestY ) {
            return new DrawableItemEvent( DrawableItemEvent.DEAD, mX, mY );
        }

        return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
    }

    @Override
    public int onTouchEvent( MotionEvent event ) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if ( isHit( (int)event.getX(), (int)event.getY())) {
                    if ( ! mIsRunningAway ) {
                        ThreadGaming.playAudioSfx( ThreadGaming.AUDIO_CARTON_BOING );
                        setDestination( mX, mScreenHeight, System.currentTimeMillis(), RUN_AWAY_DURATION);
                    }
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

        return 0;
    }
}
