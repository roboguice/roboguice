package org.roboguice.astroboy.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.roboguice.astroboy.R;
import roboguice.RoboGuice;
import roboguice.inject.InjectView;

public class TVStatusView extends LinearLayout {

    @InjectView(R.id.tv_status)
    private TextView textviewStatus;

    public TVStatusView(Context context) {
        super(context);
        initializeView(context);
        RoboGuice.injectMembers(context, this);
        RoboGuice.getInjector(context).injectViewMembers(this);
        onFinishInflate();
    }

    public TVStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public TVStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeView(context);
    }

    public void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_tv_status, this, true);
    }

    public void updateStatus(String status) {
        textviewStatus.setText(status);
    }
}
