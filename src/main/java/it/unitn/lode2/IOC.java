package it.unitn.lode2;

import java.util.HashMap;

/**
 * From zoefx-core: https://github.com/axiastudio/zoefx-core/blob/master/src/com/axiastudio/zoefx/core/Utilities.java
 * User: tiziano
 * Date: 18/03/14
 * Time: 20:46
 */
public class IOC {

    private static HashMap<String, HashMap<String, Object>> utilities = new HashMap<>();

    /**
     * Registers the unnamed utility for the given interface.
     *
     * @param utility The utility object to register
     * @param iface The interface implemented by the utility
     *
     */
    public static synchronized void registerUtility(Object utility, Class iface){
        IOC.registerUtility(utility, iface, ".");

    }

    /**
     * Registers the named utility for the given interface.
     *
     * @param utility The utility object to register
     * @param iface The interface implemented by the utility
     * @param name The string name
     *
     */
    public static synchronized void registerUtility(Object utility, Class iface, String name){
        HashMap<String, Object> hm = IOC.utilities.get(iface.getSimpleName());
        if( hm == null ){
            hm = new HashMap<>();
        }
        hm.put(name, utility);
        IOC.utilities.put(iface.getSimpleName(), hm);
    }

    /**
     * Query the unnamed utility with the given interface.
     *
     * @param iface The interface implemented by the utility
     * @return  The utility
     *
     */
    public static <T> T queryUtility(Class<T> iface){
        return IOC.queryUtility(iface, ".");
    }

    /**
     * Query the named utility with the given interface.
     *
     * @param iface The interface implemented by the utility
     * @param name The string name
     * @return  The utility
     *
     */
    public static <T> T queryUtility(Class<T> iface, String name){
        T utility = null;
        HashMap<String, Object> hm = IOC.utilities.get(iface.getSimpleName());
        if( hm != null ){
            utility = (T) hm.get(name);
        }
        return utility;
    }


}
