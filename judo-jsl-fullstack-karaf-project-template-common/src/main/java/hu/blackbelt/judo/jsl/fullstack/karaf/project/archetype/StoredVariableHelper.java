package hu.blackbelt.judo.jsl.fullstack.karaf.project.archetype;

import hu.blackbelt.judo.generator.commons.StaticMethodValueResolver;
import hu.blackbelt.judo.generator.commons.ThreadLocalContextHolder;
import hu.blackbelt.judo.generator.commons.annotations.ContextAccessor;
import hu.blackbelt.judo.generator.commons.annotations.TemplateHelper;

import java.util.Map;

/**
 * The handlebars context inaccessible in helpers / value resolvers
 * because there is no state for them. The ThreadLocal is used
 * to init variable values from template execution.
 */
@TemplateHelper
@ContextAccessor
public class StoredVariableHelper extends StaticMethodValueResolver {
    public static final String GENERATE_MODEL_MODULE = "generateModelModule";
    public static final String GENERATE_SDK_MODULE = "generateSdkModule";
    public static final String GENERATE_APPLICATION_MODULE = "generateApplicationModule";
    public static final String GENERATE_REST_MODULE = "generateRestModule";
    public static final String GENERATE_INTERNAL_MODULE = "generateInternalModule";
    public static final String GENERATE_FRONTEND_MODULE = "generateFrontendModule";
    public static final String GENERATE_E2E_MODULE = "generateE2eModule";
    public static final String GENERATE_DOCKER_MODULE = "generateDockerModule";
    public static final String GENERATE_INTERCEPTOR_MODULE = "generateInterceptorModule";
    public static final String GENERATE_SCHEMA_MODULE = "generateSchemaModule";
    public static final String GENERATE_KEYCLOAK_THEME = "generateKeycloakTheme";
    public static final String GENERATE_KARAF_MODULE = "generateKarafModule";
    public static final String GENERATE_LAUNCHER_MODULE = "generateLauncherModule";
    public static final String GENERATE_SINGLE_ACTOR_APPLICATION = "generateSingleActorApplication";
    public static final String GENERATE_WEBROOT_MODULE = "generateWebrootModule";
    public static final String GENERATE_DEFAULT_WEBROOT_CONTENT = "generateWebrootContent";
    public static final String GENERATE_OPEN_API_ANNOTATIONS = "generateOpenApiAnnotations";
    public static final String BASE_URL = "baseUrl";
    public static final String AUTHENTICATION_URL = "authenticationUrl";
    public static final String SPECIFICATION_VERSION_NUMBER = "specificationVersionNumber";

    public static void bindContext(Map<String, ?> context) {
        ThreadLocalContextHolder.bindContext(context);
    }

    public static synchronized Boolean shouldGenerateE2EModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_E2E_MODULE) == null) {
            return false;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_E2E_MODULE));
    }

    public static synchronized Boolean shouldGenerateDockerModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_DOCKER_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_DOCKER_MODULE));
    }

    public static synchronized Boolean shouldGenerateInterceptorModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_INTERCEPTOR_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_INTERCEPTOR_MODULE));
    }

    public static synchronized Boolean shouldGenerateSchemaModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_SCHEMA_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_SCHEMA_MODULE));
    }

    public static synchronized Boolean shouldGenerateKeycloakTheme() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_KEYCLOAK_THEME) == null) {
            return false;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_KEYCLOAK_THEME));
    }

    public static synchronized Boolean shouldGenerateKarafModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_KARAF_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_KARAF_MODULE));
    }

    public static synchronized Boolean shouldGenerateLauncherModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_LAUNCHER_MODULE) == null) {
            return false;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_LAUNCHER_MODULE));
    }

    public static synchronized Boolean shouldGenerateModelModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_MODEL_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_MODEL_MODULE));
    }

    public static synchronized Boolean shouldGenerateSdkModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_SDK_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_SDK_MODULE));
    }

    public static synchronized Boolean shouldGenerateApplicationModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_APPLICATION_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_APPLICATION_MODULE));
    }

    public static synchronized Boolean shouldGenerateRestModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_REST_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_REST_MODULE));
    }

    public static synchronized Boolean shouldGenerateInternalModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_INTERNAL_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_INTERNAL_MODULE));
    }

    public static synchronized Boolean shouldGenerateFrontendModule() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_FRONTEND_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_FRONTEND_MODULE));
    }

    public static synchronized Boolean shouldGenerateSingleActorApplication() {
        if (ThreadLocalContextHolder.getVariable(GENERATE_WEBROOT_MODULE) != null &&
                Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_DEFAULT_WEBROOT_CONTENT))) {
            throw new IllegalArgumentException("Do not use 'generateSingleActorApplication' with 'generateWebrootModule'");
        }

        if (ThreadLocalContextHolder.getVariable(GENERATE_SINGLE_ACTOR_APPLICATION) == null) {
            return false;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_SINGLE_ACTOR_APPLICATION));
    }

    public static synchronized Boolean shouldGenerateWebrootModule() {
        if (shouldGenerateSingleActorApplication()) {
            return false;
        }
        if (ThreadLocalContextHolder.getVariable(GENERATE_WEBROOT_MODULE) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_WEBROOT_MODULE));
    }

    public static synchronized Boolean shouldGenerateDefaultWebrootContent() {
        if (!shouldGenerateWebrootModule()) {
            return false;
        }
        if (ThreadLocalContextHolder.getVariable(GENERATE_DEFAULT_WEBROOT_CONTENT) == null) {
            return true;
        }
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_DEFAULT_WEBROOT_CONTENT));
    }

    public static synchronized Boolean isGenerateOpenApiAnnotations() {
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable(GENERATE_OPEN_API_ANNOTATIONS));
    }

    public static synchronized String getBaseUrl() {
        return (String) ThreadLocalContextHolder.getVariable(BASE_URL);
    }

    public static synchronized String getAuthenticationUrl() {
        return (String) ThreadLocalContextHolder.getVariable(AUTHENTICATION_URL);
    }

    public static synchronized String getSpecificationVersionNumber() {
        return (String) ThreadLocalContextHolder.getVariable(SPECIFICATION_VERSION_NUMBER);
    }
}
