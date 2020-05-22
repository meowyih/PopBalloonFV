package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

/**
 * Created by yhorn on 2016/2/21.
 */
public class ItemRainDrop extends ItemMoving {

    final static private String appTag = "ItemRainDrop";

    // debug: recycle counter
    static public int mBitmapRecycleCount = 0;

    public ItemRainDrop(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);
    }

    @Override
    DrawableItemEvent move(long time) {

        // Log.v( appTag, "before mInitx:" + mInitX + " mInitY:" + mInitY + " mX:" + mX + " mY:" + mY + " destX:" + mDestX + " destY:" + mDestY  );
        super.move(time);
        // Log.v( appTag, "after mInitx:" + mInitX + " mInitY:" + mInitY + " mX:" + mX + " mY:" + mY + " destX:" + mDestX + " destY:" + mDestY  );

        if ( mY >= mDestY ) {
            // Log.v( appTag, "mInitx:" + mInitX + " mInitY:" + mInitY + " mX:" + mX + " mY:" + mY + " destX:" + mDestX + " destY:" + mDestY  );
            return new DrawableItemEvent( DrawableItemEvent.DEAD, mX, mY );
        }
        else {
            return new DrawableItemEvent( DrawableItemEvent.NONE, mX, mY );
        }
    }

}
