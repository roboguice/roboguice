package org.roboguice.astroboy.provided;

import org.roboguice.astroboy.R;

import roboguice.fragment.provided.RoboListFragment;
import roboguice.inject.InjectResource;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * A list fragment representing a list of Asteroids. 
 */
public class AsteroidListFragment extends RoboListFragment {
	@InjectResource(R.array.asteroids) public String[] asteroids;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1,
				android.R.id.text1,asteroids));
	}
}
