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
    public static void bindContext(Map<String, ?> context) {
        ThreadLocalContextHolder.bindContext(context);
    }

    public static synchronized Boolean shouldGenerateE2EModule() {
        return Boolean.parseBoolean((String) ThreadLocalContextHolder.getVariable("generateE2EModule"));
    }
}
