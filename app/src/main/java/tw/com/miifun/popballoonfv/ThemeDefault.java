package tw.com.miifun.popballoonfv;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.core.content.ContextCompat;

/**
 * Created by yhorn on 2016/2/19.
 */
public class ThemeDefault extends Theme {

    private long DURATION_IN = 5000;
    private long DURATION_OUT = 5000;
    private Context mContext;
    private int mScreenWidth;
    private int mScreenHeight;

    public ThemeDefault( Context context, int screenWidth, int screenHeight ) {
        super( context, screenWidth, screenHeight );

        mContext = context;
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
    }

    @Override
    public int id() { return THEME_DEFAULT; }

    @Override
    public long durationIn() {
        return DURATION_IN;
    }

    @Override
    public long durationOut() {
        return DURATION_OUT;
    }

    @Override
    public DrawableItemEvent move( long time ) {
        return new DrawableItemEvent( DrawableItemEvent.NONE, 0, 0 );
    }

    @Override
    public boolean drawTheme( Canvas canvas ) {
        Paint paint = new Paint();
        Rect rect;

        // draw background color
        rect = new Rect( 0, 0, mScreenWidth, mScreenHeight );
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ContextCompat.getColor(mContext, R.color.ThemeDefaultBackground));
        canvas.drawRect(rect, paint);

        return true;
    }

    @Override
    public void destroy() {

    }
}
