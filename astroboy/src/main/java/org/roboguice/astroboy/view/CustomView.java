package org.roboguice.astroboy.view;

import org.roboguice.astroboy.R;

import roboguice.inject.InjectView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomView extends LinearLayout {

    @InjectView(R.id.close_tv)
    private Button buttonCloseTv;
    @InjectView(R.id.tv_status)
    private TextView textviewStatus;

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView(context);
    }

    public CustomView(Context context) {
        super(context);
        initializeView(context);
    }

    public void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_custom, this, true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        buttonCloseTv.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                textviewStatus.setText("Closed");
            }
        });
        textviewStatus.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                textviewStatus.setText("Open");
            }
        });
    }
}
