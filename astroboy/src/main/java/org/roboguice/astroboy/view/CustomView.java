package org.roboguice.astroboy.view;

import butterknife.Bind;
import butterknife.ButterKnife;
import org.roboguice.astroboy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomView extends LinearLayout {

    @Bind(R.id.close_tv)
    Button buttonCloseTv;
    @Bind(R.id.tv_status)
    TextView textviewStatus;

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
        ButterKnife.bind(this);
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
