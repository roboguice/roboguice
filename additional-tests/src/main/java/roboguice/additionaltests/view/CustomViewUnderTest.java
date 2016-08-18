package roboguice.additionaltests.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import roboguice.RoboGuice;
import roboguice.inject.InjectView;

import static android.os.Build.VERSION_CODES.HONEYCOMB;

public class CustomViewUnderTest extends FrameLayout {
    @Inject public Vibrator vibrator;
    @InjectView(100) public TextView textView;

    public CustomViewUnderTest(Context context) {
        this(context, null, 0);
        RoboGuice.getInjector(context).injectMembers(this);
        RoboGuice.getInjector(context).injectViewMembers(this);
        onFinishInflate();
    }

    @SuppressWarnings("unused")
    public CustomViewUnderTest(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(HONEYCOMB)
    public CustomViewUnderTest(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        final TextView v = new TextView(context,attrs,defStyle);
        v.setId(100);
        addView(v);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();
        textView.setText("Foo");
    }
}
