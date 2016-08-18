package org.roboguice.astroboy.view;

import android.annotation.TargetApi;
import android.os.Build;
import javax.inject.Inject;
import org.roboguice.astroboy.R;

import roboguice.RoboGuice;
import roboguice.event.EventManager;
import roboguice.inject.InjectView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OpenCloseTVView extends LinearLayout {

    @InjectView(R.id.open_tv)
    private Button buttonOpenTv;
    @InjectView(R.id.close_tv)
    private Button buttonCloseTv;
    @Inject
    EventManager eventManager;

    public OpenCloseTVView(Context context) {
        super(context);
        initializeView(context);
        RoboGuice.injectMembers(context, this);
        RoboGuice.getInjector(context).injectViewMembers(this);
        onFinishInflate();
    }

    public OpenCloseTVView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public OpenCloseTVView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeView(context);
    }

    public void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_open_close_tv, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        buttonCloseTv.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                eventManager.fire(new TVEvent("Closed"));
            }
        });
        buttonOpenTv.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                eventManager.fire(new TVEvent("Open"));
            }
        });
    }
}
