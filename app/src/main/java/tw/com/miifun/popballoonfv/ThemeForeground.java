package tw.com.miifun.popballoonfv;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by yhorn on 2016/2/23.
 */
public class ThemeForeground extends Theme implements ConfirmDialog.ConfirmDialogListener {

    final static private String appTag = "ThemeForeground";

    boolean mRequestExitApplication = false;
    boolean mIsFragmentConfirmInFront = false;

    ConfirmDialog mConfirmDialog = null;

    ItemMoving mExit;
    ItemScore mScore;
    ItemSpeedMeter mSpeed;

    public ThemeForeground( Context context, int screenWidth, int screenHeight ) {
        super( context, screenWidth, screenHeight );

        int x, y;
        int height, width;

        // exit button
        height = mScreenHeight / 5;
        width = height * context.getResources().getInteger( R.integer.icon_exit_width ) /
                context.getResources().getInteger( R.integer.icon_exit_height );
        x = 0;
        y = mScreenHeight - height;
        mExit = new ItemMoving( context, 0, x, y, width, height, mScreenWidth, mScreenHeight );
        mExit.setBitmap( R.drawable.icon_exit );

        // score
        height = mScreenHeight / 10;
        width = 3* height * mContext.getResources().getInteger( R.integer.numeric_character_width ) /
                mContext.getResources().getInteger(R.integer.numeric_character_height);
        x = 0 + mContext.getResources().getDimensionPixelSize(R.dimen.game_speed_up_margin_right);
        y = 0 + mContext.getResources().getDimensionPixelSize( R.dimen.game_speed_up_margin_top );
        mScore = new ItemScore( context, 0, x, y, width, height, mScreenWidth, mScreenHeight );
        mScore.setScore( 0 );

        // speed up
        height = mScreenHeight / 10;
        width = ItemSpeedMeter.estimateWidthByHeight( mContext, height );
        x = mScreenWidth - width - mContext.getResources().getDimensionPixelSize( R.dimen.game_speed_up_margin_right );
        y = 0 + mContext.getResources().getDimensionPixelSize( R.dimen.game_speed_up_margin_top );
        mSpeed = new ItemSpeedMeter( context, 0, x, y, width, height, mScreenWidth, mScreenHeight );
    }

    public int id() { return THEME_BACKGROUND; }

    public void setScore( int score ) {
        mScore.setScore( score );
    }
    public void setSpeed( int speed ) { mSpeed.setSpeed( speed );}

    public int getSpeedLevel() {
        return mSpeed.getSpeed();
    }

    public void setMaxSpeedLevel( int maxLevel ) {
        mSpeed.setMaxLevel( maxLevel );
    }

    public DrawableItemEvent move( long time ) {

        DrawableItemEvent event;

        if ( mRequestExitApplication )
            return new DrawableItemEvent( DrawableItemEvent.EXIT_APPLICATION, 0, 0 );

        mExit.move( time );
        event = mSpeed.move( time );

        // two global event that game thread needs to be aware of it
        if ( event.type == DrawableItemEvent.SPEED_UP ||
                event.type == DrawableItemEvent.SPEED_DOWN ) {
            return event;
        }
        return new DrawableItemEvent( DrawableItemEvent.NONE, 0, 0 );
    }

    private void startConfirmDialog(int dialogType, int titleResId, int descResId) {
        if (mConfirmDialog == null) {
            // it is annoying to show all error dialog for each track
            FragmentManager fm = ((Activity)mContext).getFragmentManager();
            mConfirmDialog = new ConfirmDialog();
            mConfirmDialog.setListener( this );
            Bundle bundle = new Bundle();
            bundle.putInt(ConfirmDialog.PARAM_DIALOG_TYPE, dialogType);
            bundle.putString(ConfirmDialog.PARAM_DIALOG_TITLE, mContext.getResources().getString(titleResId));
            bundle.putString(ConfirmDialog.PARAM_DIALOG_DESC, mContext.getResources().getString(descResId));

            mConfirmDialog.setArguments(bundle);
            mIsFragmentConfirmInFront = true;
            mConfirmDialog.show(fm, "startConfirmDialog");
        } else {
            Log.w(appTag, "warning, cannot display dialog since it exists " + mConfirmDialog);
        }
    }

    public boolean drawTheme( Canvas canvas ) {
        mExit.draw( canvas );
        mScore.draw( canvas );
        mSpeed.draw( canvas );
        return true;
    }

    public void destroy() {
        if ( mExit != null )
            mExit.destroy();
        mExit = null;

        if ( mScore != null )
            mScore.destroy();
        mScore = null;

        if ( mSpeed != null )
            mSpeed.destroy();
        mSpeed = null;
    }

    // MotionEvent handler
    public void onTouchEvent( MotionEvent event ) {

        int action = event.getAction();
        DrawableItemEvent dEvent;
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                if ( mExit.isHit( (int) event.getX(), (int) event.getY() )) {
                    mExit.setBitmap( R.drawable.icon_exit_pressed );
                }
                mSpeed.onTouchEvent( event );
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if ( mExit.isHit( (int) event.getX(), (int) event.getY() )) {
                    mExit.setBitmap( R.drawable.icon_exit );
                    startConfirmDialog(  ConfirmDialog.TYPE_EXIT, R.string.title_exit, R.string.desc_exit );
                }
                mSpeed.onTouchEvent( event );
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_OUTSIDE:
                break;
            default:
        }
    }

    /*
     * ConfirmDialog.ConfirmDialogListener
     */
    @Override
    public void onFinishConfirmDialog(int type, int result) {
        Log.v(appTag, "onFinishConfirmDialog " + type + " " + result);
        mIsFragmentConfirmInFront = false;
        if (type == ConfirmDialog.TYPE_EXIT) {
            if (result == ConfirmDialog.POSITIVE)
                mRequestExitApplication = true;
        }

        mConfirmDialog = null;
    }
}
