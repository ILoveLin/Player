package com.company.shenzhou.widget.vlc;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.company.shenzhou.R;

/**
 * LoveLin
 * <p>
 * Describe
 */
public class PlayerViewLine2 extends RelativeLayout implements GestureDetector.OnGestureListener {
    private Context mContext;
    private TextView mRootLayout;
    //    private RelativeLayout mRootLayout;
    private LinearLayout mVideoGestureLayout;
    private ImageView mVideoGestureImg;
    private TextView mVideoGestureText;

    public PlayerViewLine2(Context context) {
        super(context);
        initView(context);
    }

    public PlayerViewLine2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PlayerViewLine2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.player_view_line3_steam2_layout, this);
        //初始化控件
        initControlView();
        mVideoGestureLayout.getBackground().setAlpha(110);
//        mRootLayout.setOnTouchListener(mOnTouchVideoListener);
//        mRootLayout.setLongClickable(true);  //手势需要
        //视频播放器手势
        mGestureDetector = new GestureDetector(mContext, this);
        mGestureDetector.setIsLongpressEnabled(true);
        mAudiomanager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
    }

    public TextView getRootView() {
        return mRootLayout;
    }

    public OnTouchListener getOnTouchVideoListener() {
        return mOnTouchVideoListener;
    }


    private AudioManager mAudiomanager;

    private void initControlView() {
        mRootLayout = findViewById(R.id.web_view_onclick);
        mVideoGestureLayout = findViewById(R.id.video_gesture_layout);
        mVideoGestureImg = findViewById(R.id.video_gesture_img);
        mVideoGestureText = findViewById(R.id.video_gesture_text);
    }

    //播放器手势
    private GestureDetector mGestureDetector;
    private boolean mIsProgressChange = false;    //是否为手势改变进度
    private boolean mIsFirstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
    private int GESTURE_FLAG = 0;// 1,调节进度，2，调节音量,3.调节亮度
    //private static final int GESTURE_MODIFY_PROGRESS = 1;
    private static final int GESTURE_MODIFY_VOLUME = 2;
    private static final int GESTURE_MODIFY_BRIGHT = 3;
    private int mMaxVolume = -1;
    private int mCurrentVolume = -1;
    private float mCurrentBrightness = 0.5f;
    private float mMaxBrightness = 255.0f;
    private static final int MIN_BRIGHTNESS = 10;
    //private long mCurDownPlayingTime = 0;
    private float mDownX = 0;
    private float mDownY = 0;
    /*视频播放 - Start*/
    private OnTouchListener mOnTouchVideoListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                mDownX = motionEvent.getX();
                mDownY = motionEvent.getY();
                mIsFirstScroll = true;  // 设定是触摸屏幕后第一次scroll的标志
                mCurrentVolume = mAudiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前音量值
                //第一次进入，获取的当前亮度为系统目前亮度（此时getWindow().getAttributes().screenBrightness = -1.0）
                //未退出再次在该界面调节时，获取当前已调节的亮度
                if (((Activity) mContext).getWindow().getAttributes().screenBrightness < 0) {
                    mCurrentBrightness = android.provider.Settings.System.getInt(mContext.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS, 255) / mMaxBrightness;  // 获取当前系统亮度值,获取失败则返回255
                } else {
                    mCurrentBrightness = ((Activity) mContext).getWindow().getAttributes().screenBrightness;
                }
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                float upX = motionEvent.getX();
                float upY = motionEvent.getY();
                GESTURE_FLAG = 0;// 手指离开屏幕后，重置调节音量或进度的标志
                mVideoGestureLayout.setVisibility(GONE);
                //通过down和up来判断手势是否移动，部分机型用MotionEvent.ACTION_MOVE判断会有误
                if (Math.abs(upX - mDownX) > 20 || Math.abs(upY - mDownY) > 20) {
                } else {  //非手势移动，才自动显示/隐藏状态栏
                }
                mIsProgressChange = false;
            }
            return mGestureDetector.onTouchEvent(motionEvent);
        }
    };

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    private boolean mIsAllowGesture = true; //默认允许手势操作

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!mIsAllowGesture) {
            return false;
        }
        float ex = e1.getX(), ey = e1.getY();
        int varX = (int) (e2.getX() - e1.getX());
        int varY = (int) (e2.getY() - e1.getY());
        if (mIsFirstScroll) {// 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
            mVideoGestureLayout.setVisibility(VISIBLE);
            // 横向的距离变化大则调整进度，纵向的变化大则调整音量
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {    //调节进度
                mVideoGestureLayout.setVisibility(GONE);

            } else {
                if (ex < mRootLayout.getWidth() / 2) {     //左半边亮度
                    GESTURE_FLAG = GESTURE_MODIFY_BRIGHT;
                } else {    //右半边音量
                    GESTURE_FLAG = GESTURE_MODIFY_VOLUME;
                }
            }
        }
        if (GESTURE_FLAG == GESTURE_MODIFY_BRIGHT) {
            mVideoGestureImg.setImageResource(R.drawable.brightness);
            int slideHeight = mRootLayout.getHeight() / 2;
            int midLevelPx = slideHeight / 15;
            int slideDistance = -varY;
            int slideLevel = slideDistance / midLevelPx;
            if (mCurrentBrightness == -1 || mCurrentBrightness < 0) {
                mCurrentBrightness = 0;
            }
            WindowManager.LayoutParams lpa = ((Activity) mContext).getWindow().getAttributes();
            float midLevelBright = (mMaxBrightness - MIN_BRIGHTNESS) / 15.0f;
            float realBright = midLevelBright * slideLevel + mCurrentBrightness
                    * (mMaxBrightness - MIN_BRIGHTNESS) + MIN_BRIGHTNESS;
            if (realBright < MIN_BRIGHTNESS) {
                realBright = MIN_BRIGHTNESS;
            }
            if (realBright > mMaxBrightness) {
                realBright = mMaxBrightness;
            }
            lpa.screenBrightness = realBright / mMaxBrightness;
            ((Activity) mContext).getWindow().setAttributes(lpa);
            mVideoGestureText.setText((int) (lpa.screenBrightness * 100) + "%");
        } else if (GESTURE_FLAG == GESTURE_MODIFY_VOLUME) {
            if (Math.abs(distanceY) > Math.abs(distanceX)) {// 纵向移动大于横向移动
                int slideHeight = mRootLayout.getHeight() / 2;
                int midLevelPx = slideHeight / 15;
                int slideDistance = -varY;
                int slideLevel = slideDistance / midLevelPx;
                int midLevelVolume = mMaxVolume / 15;
                int realVolume = midLevelVolume * slideLevel + mCurrentVolume;
                if (realVolume <= 0) {
                    realVolume = 0;
                    mVideoGestureImg.setImageResource(R.drawable.volume_slience);
                } else {
                    mVideoGestureImg.setImageResource(R.drawable.volume_not_slience);
                }
                if (realVolume > mMaxVolume) {
                    realVolume = mMaxVolume;
                }
                int percentage = (realVolume * 100) / mMaxVolume;
                mVideoGestureText.setText(percentage + "%");
                //设置音量大小，第一个参数：STREAM_VOICE_CALL(通话)、STREAM_SYSTEM(系统声音)、STREAM_RING(铃声)、STREAM_MUSIC(音乐)和STREAM_ALARM(闹铃)；
                // 第二个参数：音量值，取值范围为0-7；
                // 第三个参数：可选标志位，用于显示出音量调节UI(AudioManager.FLAG_SHOW_UI)。
                mAudiomanager.setStreamVolume(AudioManager.STREAM_MUSIC, realVolume, 0);
            }
        }
        mIsFirstScroll = false;// 第一次scroll执行完成，修改标志
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}