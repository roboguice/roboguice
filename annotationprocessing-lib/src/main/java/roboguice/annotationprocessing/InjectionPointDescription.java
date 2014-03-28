package roboguice.annotationprocessing;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes an injection point so that it can be use by Guice. 
 * @author SNI
 */
public class InjectionPointDescription {

    private String className;
    private List<String> listOfFieldNames = new ArrayList<String>();
    
    public InjectionPointDescription(String className) {
        this.className = className;
    }
    
    public InjectionPointDescription(String className, List<String> listOfFieldNames) {
        this.className = className;
        this.listOfFieldNames = listOfFieldNames;
    }

    public void addField(String name) {
        listOfFieldNames.add(name);
    }
    
    public String getClassName() {
        return className;
    }
    
    public List<String> getListOfFieldNames() {
        return listOfFieldNames;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        //hacky
        if (getClass() != obj.getClass() || String.class != obj.getClass())
            return false;
        //hacky
        if( obj instanceof String) {
            return ((String)obj).equals(className);
        }
        InjectionPointDescription other = (InjectionPointDescription) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "InjectionPointDescription [className=" + className + ", listOfFieldNames=" + listOfFieldNames.toString() + "]";
    }
    
}
