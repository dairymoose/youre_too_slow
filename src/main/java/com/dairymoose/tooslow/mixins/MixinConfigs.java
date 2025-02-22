package com.dairymoose.tooslow.mixins;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraftforge.common.ForgeConfigSpec;

public class MixinConfigs implements IMixinConfigPlugin {

	private static final Logger LOGGER = LogManager.getLogger();
	
    //TODO: this does not work anymore cause directory is not valid for some reason (???)
    public static List<String> getMixinClassesNames() {
        try {
            String className = MixinConfigs.class.getName();
            String packageName = MixinConfigs.class.getPackage().getName();
            LOGGER.debug("getMixinClassesNames");
            return getClassesInPackage(packageName).stream()
                    .filter(s -> !s.equals(className)).map(s -> s.substring(packageName.length() + 1))
                    //.filter(s->!s.equals(className) && !s.contains("$"))
                    //.map(s->s.substring(packageName.length() + 1))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Could not fetch mixin classes, giving up: " + e.getMessage());
            return Collections.emptyList();
        }
    }



    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return fully qualified class name strings
     * @see <a href="https://stackoverflow.com/a/520344">Source</a>
     */
    private static List<String> getClassesInPackage(String packageName) throws IOException, URISyntaxException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replaceAll("[.]", "/");

        Enumeration<URL> resources = classLoader.getResources(path);

        List<File> dirs = new ArrayList<>();

        try {
            URL classLoaderResource = classLoader.getResource(path);
            assert classLoaderResource != null;
            File dir = new File(classLoaderResource.toURI().toURL().getFile());
            dirs.add(dir);
        }catch (Exception exception){};


        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            //dirs.add(new File(resource.toURI()));
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<String> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        LOGGER.debug("getClassesInPackage");
        return classes;
    }

    public static List<String> getClassesInPackage2(String packageName) throws IOException, URISyntaxException {
        String path = packageName.replace(".", "/");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;

        ArrayList<String> classes = new ArrayList<>();
        URL classLoaderResource = classLoader.getResource(path);
        if (classLoaderResource == null)
            throw new IOException("Could not create class loader resource URL for package: " + packageName);

        //URL url = new URL(classLoaderResource.toString());

        File dir = new File(classLoaderResource.toURI());
        classes.addAll(findClasses(dir, packageName));

        LOGGER.debug("getClassesInPackage2");
        return classes.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return fully qualified class name strings
     * @see <a href="https://stackoverflow.com/a/520344">Source</a>
     */
    //TODO: this is not working anymore
    private static List<String> findClasses(File directory, String packageName) {
        List<String> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
            }
        }
        LOGGER.debug("findClasses");
        return classes;
    }


    public List<String> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        List<Class> classes = reader.lines()

                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .collect(Collectors.toList());

        LOGGER.debug("findAllClassesUsingClassLoader");
        return classes.stream().map(Class::getName).collect(Collectors.toList());

    }

    private Class getClass(String className, String packageName) {
    	LOGGER.debug("getClass");
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }


    @Override
    public void onLoad(String mixinPackage) {
    	LOGGER.debug("onLoad");
        try {
            //RegistryConfigs.createSpec();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to create registry configs: " + exception);
        }

        try {
            //RegistryConfigs.load();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load config supplementaries-registry.toml. Try deleting it");
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        //String truncatedName = mixinClassName.substring(this.getClass().getPackage().getName().length() + 1);
        //ForgeConfigSpec.BooleanValue config = RegistryConfigs.reg.MIXIN_VALUES.get(truncatedName);
        //return config == null || config.get();
    	LOGGER.debug("Checking mixin settings for: " + mixinClassName);
    	return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}