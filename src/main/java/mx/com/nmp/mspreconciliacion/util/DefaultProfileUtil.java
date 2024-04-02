package mx.com.nmp.mspreconciliacion.util;


import org.springframework.boot.SpringApplication;
import org.springframework.core.env.Environment;

import mx.com.nmp.mspreconciliacion.config.Constants;

import java.util.HashMap;
import java.util.Map;


/**
 * Nombre: DefaultProfileUtil
 * Descripcion: Establece el profile default de acuerdo a la variable de ambiente

 * @author: Javier Hernandez Barraza jhernandez@quarksoft.net
 * @version: 0.1
 */
public class DefaultProfileUtil {

    private static final String SPRING_PROFILE_DEFAULT = "spring.profiles.default";

    private DefaultProfileUtil() {
    }

    /**
     * Establece "dev" como profile por defecto
     *
     * @param app applicación de Spring
     */
    public static void addDefaultProfile(SpringApplication app) {
        Map<String, Object> defProperties = new HashMap<>();

        defProperties.put(SPRING_PROFILE_DEFAULT, Constants.SPRING_PROFILE_DEVELOPMENT);
        app.setDefaultProperties(defProperties);
    }

    /**
     * Profiles de Spring que se aplicaron o el default
     *
     * @param env ambiente de Spring
     * @return profiles
     */
    public static String[] getActiveProfiles(Environment env) {
        String[] profiles = env.getActiveProfiles();
        if (profiles.length == 0) {
            return env.getDefaultProfiles();
        }
        return profiles;
    }
}
