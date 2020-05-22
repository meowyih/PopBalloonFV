package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/19.
 */
abstract public class Theme {

    // regular theme
    final static int THEME_DEFAULT = 0;
    final static int THEME_SUNNY = 1;
    final static int THEME_NIGHT = 2;
    final static int THEME_RAINY = 3;
    final static int THEME_RAINBOW = 4;
    final static int THEME_GRID = 5;
    final static int THEME_DAY = 6;

    // special thmem
    final static int THEME_FRONT_SFX = 512;
    final static int THEME_BACK_SFX = 513;
    final static int THEME_BACKGROUND = 1024;

    final static int STATE_UNKNOWN = 0;
    final static int STATE_IN = 1;
    final static int STATE_RUN = 2;
    final static int STATE_OUT = 3;

    int mState = STATE_UNKNOWN;

    Context mContext;
    int mScreenWidth;
    int mScreenHeight;

    public class ItemData {

        final static public int ITEM_MOVING = 0;
        final static public int RAINY_CLOUD = 1;

        ItemMoving item;
        int x, y, w, h, xIn, yIn, xOut, yOut;
        int custom;

        public ItemMoving createItem() {
            item = new ItemMoving( mContext, 0, xIn, yIn, w, h, mScreenWidth, mScreenHeight );
            return item;
        }

        public ItemMoving createItem( int type ) {
            switch( type ) {
                case RAINY_CLOUD:
                    item = new ItemRainyCloud( mContext, 0, xIn, yIn, w, h, mScreenWidth, mScreenHeight );
                    break;
                default:
                    item = new ItemMoving( mContext, 0, xIn, yIn, w, h, mScreenWidth, mScreenHeight );
            }
            return item;
        }
    }

    public Theme( Context context, int screenWidth, int screenHeight ) {
        mContext = context;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
    }

    abstract public int id();

    public void startThemeIn( long time ) { return; }
    public long durationIn() { return 0; }

    public void startThemeOut( long time )  { return; }
    public long durationOut() { return 0; }

    public void skipThemeIn( long time ) { return; }

    abstract public DrawableItemEvent move( long time );
    abstract public boolean drawTheme( Canvas canvas );

    abstract public void destroy();

    // MotionEvent handler
    public void onTouchEvent( MotionEvent event ) {
        /*
        int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
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
        */
        return;
    }
}
