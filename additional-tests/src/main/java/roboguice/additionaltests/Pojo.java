package roboguice.additionaltests;

import javax.inject.Inject;

public class Pojo {
	private @Inject Foo foo;

	public Foo getFoo() {
		return foo;
	}
}
