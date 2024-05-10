package noredraw.utils;

import noredraw.core.annotation.Named;
import lombok.extern.slf4j.Slf4j;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class NamedClassUtils {
    public static <T> Map<String, T> findNamedClassesOf(Class<T> clazz) {
        ConfigurationBuilder config = new ConfigurationBuilder()
                .addUrls(ClasspathHelper.forJavaClassPath())
                .addScanners(Scanners.SubTypes);

        return new Reflections(config)
                .getSubTypesOf(clazz).stream()
                .filter(ReflectionUtils.withAnnotation(Named.class))
                .map(NamedClassUtils::createInstanceIfPossible)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(NamedClassUtils::name, Function.identity(), (t, t2) -> {
                    throw new RuntimeException("Two classes with same name found: " + t + ", " + t2);
                }));
    }

    private static <T> Optional<T> createInstanceIfPossible(Class<T> clazz) {
        try {
            Constructor<? extends T> constructor = clazz.getConstructor();
            return Optional.of(constructor.newInstance());
        } catch (NoSuchMethodException e) {
            log.error("Can't find default constructor for class {}. Skipping.", clazz);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            log.error("Can't instantiate class {}. Skipping.", clazz, e);
        }
        return Optional.empty();
    }

    private static <T> String name(T instance) {
        return instance.getClass().getAnnotation(Named.class).value().toLowerCase();
    }
}
