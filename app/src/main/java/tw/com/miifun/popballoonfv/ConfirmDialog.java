package tw.com.miifun.popballoonfv;

import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yhorn on 2016/2/13.
 */
public class ConfirmDialog extends DialogFragment implements View.OnClickListener {

    final private String appTag = "ConfirmDialog";

    public interface ConfirmDialogListener {
        void onFinishConfirmDialog(int type, int result);
    }

    public static final String PARAM_DIALOG_TYPE = "ToolType";
    public static final String PARAM_DIALOG_TITLE = "Title";
    public static final String PARAM_DIALOG_DESC = "Desc";
    public static final String PARAM_DIALOG_ERROR_CODE ="ErrorCode";

    public static final int TYPE_EXIT = 100;
    public static final int TYPE_ERROR = 101;
    public static final int TYPE_NO_EXPANSION_NO_NETWORK = 102;
    public static final int TYPE_NO_EXPANSION_ASK_DOWNLOAD = 103;
    public static final int TYPE_NO_EXPANSION_NO_SPACE = 104;
    public static final int TYPE_ALREADY_BOUGHT = 105;
    public static final int TYPE_CANCEL_PURCHASE = 105;
    public static final int TYPE_PURCHASED = 106;

    public static final int POSITIVE = 1;
    public static final int NEGATIVE = 0;
    private int mDialogType = 0;

    ImageView mIvCat;
    Button mBtnPositive;
    Button mBtnNegative;
    TextView mTvTitle;
    TextView mTvDesc;

    String mStrTitle;
    String mStrDesc;
    String mErrorCode;

    boolean mIsOnFinishCalled = false;
    ConfirmDialogListener mListener;

    public ConfirmDialog() {
        super();
    }

    public void setListener( ConfirmDialogListener listener ) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0); // remove title from dialogfragment

        Bundle bundle = getArguments();

        if ( bundle != null ) {
            mDialogType = bundle.getInt(PARAM_DIALOG_TYPE);
            mStrTitle = bundle.getString(PARAM_DIALOG_TITLE);
            mStrDesc = bundle.getString(PARAM_DIALOG_DESC);
            mErrorCode = bundle.getString(PARAM_DIALOG_ERROR_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        View view = inflater.inflate(R.layout.fragment_confirm, container);

        mIvCat = (ImageView) view.findViewById( R.id.iv_fragment_cat );
        mBtnNegative = (Button) view.findViewById( R.id.btn_fragment_negative );
        mBtnNegative.setOnClickListener( this );
        mBtnPositive = (Button) view.findViewById( R.id.btn_fragment_positive );
        mBtnPositive.setOnClickListener( this );
        mTvTitle = (TextView) view.findViewById( R.id.tv_fragment_title );
        mTvDesc = (TextView) view.findViewById( R.id.tv_fragment_desc );

        if ( mDialogType == TYPE_EXIT ) {
            mIvCat.setImageResource(R.drawable.oops02);
        }
        else if ( mDialogType == TYPE_ERROR ) {
            mIvCat.setImageResource(R.drawable.oops03);
            mBtnPositive.setVisibility( View.GONE );
        }
        else if ( mDialogType == TYPE_NO_EXPANSION_NO_NETWORK ) {
            mIvCat.setImageResource(R.drawable.oops03);
            mBtnPositive.setVisibility(View.GONE);
        }
        else if ( mDialogType == TYPE_NO_EXPANSION_NO_SPACE ) {
            mIvCat.setImageResource(R.drawable.oops03);
            mBtnPositive.setVisibility(View.GONE);
        }
        else if ( mDialogType == TYPE_NO_EXPANSION_ASK_DOWNLOAD ) {
            mIvCat.setImageResource(R.drawable.oops02);
        }
        else if ( mDialogType == TYPE_ALREADY_BOUGHT ) {
            mIvCat.setImageResource(R.drawable.laugh);
            mBtnNegative.setVisibility(View.GONE);
        }
        else if ( mDialogType == TYPE_CANCEL_PURCHASE ) {
            mIvCat.setImageResource(R.drawable.oops02);
            mBtnNegative.setVisibility(View.GONE);
        }
        else if ( mDialogType == TYPE_PURCHASED ) {
            mIvCat.setImageResource(R.drawable.laugh);
            mBtnNegative.setVisibility(View.GONE);
        }

        if ( mErrorCode != null && mErrorCode.length() > 0 )
            mTvTitle.setText( mStrTitle + " " + mErrorCode );
        else
            mTvTitle.setText( mStrTitle );

        mTvDesc.setText(mStrDesc);

        getDialog().setCanceledOnTouchOutside(true);

        hideStatusBar();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // change the fragment width and height
        int width = getResources().getDimensionPixelSize(R.dimen.fragment_confirm_width);
        int height = getResources().getDimensionPixelSize(R.dimen.fragment_confirm_height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onDestroy() {

        if ( ! mIsOnFinishCalled ) {
            // end user might closed the fragment by nav bar without click the button
            if ( mListener != null )
                mListener.onFinishConfirmDialog(mDialogType, NEGATIVE);
            dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {

        if ( view == mBtnNegative ) {

            if ( mListener != null )
                mListener.onFinishConfirmDialog(mDialogType, NEGATIVE);
            mIsOnFinishCalled = true;
            dismiss();
        }
        else if ( view == mBtnPositive ) {

            if ( mListener != null )
               mListener.onFinishConfirmDialog(mDialogType, POSITIVE );
            mIsOnFinishCalled = true;
            dismiss();
        }
    }

    private void hideStatusBar() {

        // hide the nav bar and make it full screen
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            getDialog().getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getDialog().getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

    }
}
