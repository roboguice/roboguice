package roboguice.additionaltests.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.inject.Inject;
import roboguice.inject.InjectView;

public class ShouldInjectCustomViewsView extends FrameLayout {
    @Inject public Context context;
    @InjectView(100) public TextView textView;

    public ShouldInjectCustomViewsView(Context context) {
        this(context, null, 0);
    }

    public ShouldInjectCustomViewsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShouldInjectCustomViewsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        final TextView v = new TextView(context,attrs,defStyle);
        v.setId(100);
        addView(v);
    }
}
