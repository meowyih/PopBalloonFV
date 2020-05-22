package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import androidx.core.content.ContextCompat;

/**
 * Created by yhorn on 2016/2/21.
 */
public class ItemGrid extends ItemMoving {
    final static private String appTag = "ItemMoving";

    int mColorId;
    int mInitColorId;
    long mChangeColorTime = 0;
    long mChangeColorInterval = 0;

    public ItemGrid(Context context, int type, int x, int y, int width, int height, int screenWidth, int screenHeight, int colorId ) {
        super(context, type, x, y, width, height, screenWidth, screenHeight);
        mColorId = colorId;
        mInitColorId = colorId;
    }

    public void changeColorTemperary( int colorResId, long time, long interval ) {

        if ( mChangeColorTime > 0 )
            return;

        mColorId = colorResId;
        mChangeColorTime = time;
        mChangeColorInterval = interval;

        Log.d( appTag, "move, change color to temp color " + mColorId + " time " + mChangeColorTime + " interval " + mChangeColorInterval );
    }

    public void setBitmap( int resid ) {
        // do nothing, we dont need bitmap
    }

    @Override
    public DrawableItemEvent move( long time ) {

        if ( mChangeColorTime != 0 ) {
            if ( time - mChangeColorTime > mChangeColorInterval ) {
                Log.d( appTag, "move, change color to init color " + mInitColorId );
                mColorId = mInitColorId;
                mChangeColorTime = 0;
            }
        }

        return super.move( time );
    }

    // public abstract method
    public boolean draw( Canvas canvas ) {
        Paint paint = new Paint();
        Rect rect = new Rect( mX, mY, mX + mWidth, mY + mHeight );
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(mContext, mColorId));
        canvas.drawRect(rect, paint);
        return true;
    }

    public void destroy() {
    }
}
