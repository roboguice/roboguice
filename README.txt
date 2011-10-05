RoboGuice
---------

Please visit the site  wiki, issue tracker and more at

http://roboguice.org

and join the mailing list.

https://groups.google.com/forum/#!forum/roboguice

Build source
------------

We use Apache Maven and the Android Maven Plugin to build roboguice and the sample application.

- Install Apache Maven from http://maven.apache.org/

- Install the Android SDK

- Install the Android artifacts from the SDK in the Maven repository using the  Maven Android SDK Deployer
  https://github.com/mosabua/maven-android-sdk-deployer

  This is due maps.jar and the compatibility library not being available in any public repo.

- Run
  mvn clean install
  to build roboguice and the sample astroboy.

- Deploy astroboy on a connected devitce/emulator with
  cd astroboy
  mvn android:deploy
  and run it with
  mvn android:run

Documentation about using the Android Maven Plugin including the sdk deployer, archetypes for project creation,
issues tracker, mailing list of oon thers users  and more can be found at http://code.google.com/p/maven-android-plugin/