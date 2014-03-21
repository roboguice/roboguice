package roboguice;

import java.util.HashSet;
import java.util.Set;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * Finds all annotation databases. AnnotationDatabase can be generated using RoboGuice annotation compiler.
 * By default the roboguice annotation database is taken into account, and this can't be modified.
 * <br/>
 * You can add custom annotation databases by adding them to your manifest : 
 * <pre> 
 *  &lt;meta-data android:name="roboguice.annotations.packages"
 *    android:value="myPackage" /&gt;
 * </pre>
 * In that case, RoboGuice will load both <code>roboguice.AnnotationDatabaseImpl</code> and <code>myPackage.AnnotationDatabaseImpl</code>.
 * More packages containing AnnotationDatabases can be added, separated by commas. 
 * @author SNI
 */
public class AnnotationDatabaseFinder {
    
    private HashSet<String> classesContainingInjectionPointsSet = new HashSet<String>();
    private HashSet<String> injectedClassesSet = new HashSet<String>();

    public AnnotationDatabaseFinder(Application application) {
        Set<String> additionalPackageNameList = new HashSet<String>();
        
        try {
            ApplicationInfo ai = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
            final Bundle bundle = ai.metaData;
            final String roboguicePackages = bundle!=null ? bundle.getString("roboguice.annotations.packages") : null;
            if( roboguicePackages != null ) {
                for( String packageName : roboguicePackages.split("[\\s,]") ) {
                    additionalPackageNameList.add(packageName);
                }
            }
        } catch (NameNotFoundException e) {
            //if no packages are found in manifest, just log
            e.printStackTrace();
        }
        
        //add roboguice annotation database itself
        additionalPackageNameList.add("roboguice");
        String[] additionalPackageNames = new String[additionalPackageNameList.size()];
        additionalPackageNameList.toArray(additionalPackageNames);
        
        try {
            String annotationDatabaseClassName = "AnnotationDatabaseImpl";
            AnnotationDatabase annotationDatabase = getAnnotationDatabaseInstance(annotationDatabaseClassName);
            addAnnotationDatabase(annotationDatabase);

            for( String pkg : additionalPackageNames ) {
                annotationDatabaseClassName = pkg + ".AnnotationDatabaseImpl";
                annotationDatabase = getAnnotationDatabaseInstance(annotationDatabaseClassName);
                addAnnotationDatabase(annotationDatabase);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Set<String> getClassesContainingInjectionPoints() {
        return classesContainingInjectionPointsSet;
    }

    public Set<String> getInjectedClasses() {
        return injectedClassesSet;
    }

    private AnnotationDatabase getAnnotationDatabaseInstance(String annotationDatabaseClassName) throws ClassNotFoundException, InstantiationException,
    IllegalAccessException {
        Class<?> annotationDatabaseClass = Class.forName( annotationDatabaseClassName);
        AnnotationDatabase annotationDatabase = (AnnotationDatabase) annotationDatabaseClass.newInstance();
        return annotationDatabase;
    }

    private void addAnnotationDatabase(AnnotationDatabase annotationDatabase) {
        classesContainingInjectionPointsSet.addAll(annotationDatabase.getClassesContainingInjectionPoints());
        injectedClassesSet.addAll(annotationDatabase.getInjectedClasses());
    }

}
