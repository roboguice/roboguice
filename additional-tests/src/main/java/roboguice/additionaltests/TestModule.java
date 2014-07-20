package roboguice.additionaltests;

import android.app.Application;

import com.google.inject.AbstractModule;

/**
 * This module only has a constructor with an application argument.
 * It tests dynamic module loading.
 * @author SNI
 */
public class TestModule extends AbstractModule {

	public TestModule(Application application) {
	}
	
	@Override
	protected void configure() {
		bind(Foo.class).to(FooImpl.class);
	}

}
