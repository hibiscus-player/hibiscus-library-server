package me.mrgazdag.hibiscus.library.plugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class PluginClassLoader extends ClassLoader {
    private Path path;
    private Plugin plugin;
    private final JarFile jf;
    private final Map<String,JarEntry> entryMap;
    private PluginDescriptionFile descriptionFile;
    private final Map<String,Class<?>> classMap;

    public PluginClassLoader(Path path) throws IOException, InvalidPluginDescriptionFileException {
        this.path = path;
        this.jf = new JarFile(path.toFile());
        this.entryMap = new HashMap<>();
        this.classMap = new HashMap<>();
        Enumeration<JarEntry> e = jf.entries();
        while (e.hasMoreElements()) {
            JarEntry entry = e.nextElement();
            if (entry.getName().equals("plugin.json")) {
                try {
                    descriptionFile = createDescriptionFile(jf.getInputStream(entry));
                    continue;
                } catch (Exception exc) {
                    if (exc instanceof InvalidPluginDescriptionFileException) throw exc;
                    else throw new InvalidPluginDescriptionFileException(exc);
                }
            }
            if(entry.isDirectory() || !entry.getName().endsWith(".class")) {
                continue;
            }
            // -6 because of .class
            String className = entry.getName().substring(0,entry.getName().length()-6);
            className = className.replace('/', '.');
            entryMap.put(className,entry);
            //Class c = cl.loadClass(className);
        }
        if (descriptionFile == null) {
            throw new InvalidPluginDescriptionFileException("Missing plugin.json file");
        }
        String mainClass = descriptionFile.getMainClass();
        if (!entryMap.containsKey(mainClass)) {
            throw new InvalidPluginDescriptionFileException("Missing main class file \"" + mainClass + "\"");
        }
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public PluginDescriptionFile getDescriptionFile() {
        return descriptionFile;
    }


    @Override
    protected URL findResource(String name) {
        try {
            String spec = "jar:" + path.toUri().toString() + "!/" + name;
            //System.out.println("spec: \"" + spec + "\"");
            return URI.create(spec).toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (classMap.containsKey(name)) return classMap.get(name);
        else throw new ClassNotFoundException(name);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        JarEntry entry = entryMap.get(name);
        if (entry != null) {
            try {
                InputStream stream = jf.getInputStream(entry);
                byte[] bytes = loadClassFromFile(stream);
                Class<?> definedClass = defineClass(name, bytes, 0, bytes.length);
                classMap.put(name, definedClass);
                return definedClass;
            } catch (IOException e) {
                throw new ClassNotFoundException("IOException while loading",e);
            }
        }
        else return PluginClassLoader.class.getClassLoader().loadClass(name);
    }
    private byte[] loadClassFromFile(InputStream inputStream) throws IOException {
        byte[] buffer;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int nextValue;
        try {
            while ( (nextValue = inputStream.read()) != -1 ) {
                byteStream.write(nextValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputStream.close();
        buffer = byteStream.toByteArray();
        return buffer;
    }
    private PluginDescriptionFile createDescriptionFile(InputStream stream) throws InvalidPluginDescriptionFileException,IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        while (br.ready()) sb.append(br.readLine()).append("\n");
        br.close();
        try {
            JSONObject obj = new JSONObject(sb.toString());
            return new PluginDescriptionFile(obj);
        } catch (JSONException e) {
            throw new InvalidPluginDescriptionFileException(e);
        }
    }

    public void release() throws Exception {
        System.out.println("PluginClassLoader close method callsed");
        new Throwable().fillInStackTrace().printStackTrace();
        jf.close();
        plugin = null;
        classMap.clear();
    }
}
