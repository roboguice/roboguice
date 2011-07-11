We use maven to build roboguice.

Simply type "mvn clean install" to do a clean build of the various roboguice artifacts, including the astroboy tutorial application.

The first time you build using maven, you will be prompted to locate the android maps.jar library, which due to licensing restrictions isn't available in the public maven repositories.  To help maven locate this dependency, simply point it at the jar in your sdk by running the following command:

    mvn install:install-file \
        -DgroupId=com.google.android.maps \
        -DartifactId=maps \
        -Dversion=3_r3 \
        -Dpackaging=jar \
        -DcreateChecksum=true \
        -DgeneratePom=true \
        -Dfile=/path/to/file

Where /path/to/file is the path to maps.jar in your Android SDK.  If you don't have v3 handy, you can probably point it to just about any version



(PS. Want to start a new android project from scratch using maven? https://github.com/akquinet/android-archetypes/wiki/android-quickstart-archetype )
