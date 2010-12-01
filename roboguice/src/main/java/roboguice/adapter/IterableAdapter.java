package roboguice.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class IterableAdapter<T> extends ArrayAdapter<T> {

    public IterableAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public IterableAdapter(Context context, int resource, int textViewResourceId, Iterable<T> objects) {
        super(context, resource, textViewResourceId, toList(objects));
    }

    public IterableAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public IterableAdapter(Context context, int textViewResourceId, Iterable<T> objects) {
        super(context, textViewResourceId, toList(objects));
    }

    protected static <T> List<T> toList( Iterable<T> objects ) {
        final ArrayList<T> list = new ArrayList<T>();
        for( T t : objects ) list.add(t);
        return list;
    }
}
