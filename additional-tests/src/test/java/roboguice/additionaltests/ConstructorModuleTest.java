package roboguice.additionaltests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import roboguice.RoboGuice;

@RunWith(RobolectricTestRunner.class)
public class ConstructorModuleTest {

	@Test
	public void testShouldUseConstructorWithApplicationArgument() {
		//GIVEN
		Pojo instance = new Pojo();
		//WHEN
		RoboGuice.getOrCreateBaseApplicationInjector(Robolectric.application).injectMembers(instance);
		//THEN
		//will only work if the TestModule could be loaded properly
		assertThat( instance.getFoo(), notNullValue());
	}
}
