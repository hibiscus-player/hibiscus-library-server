package me.mrgazdag.hibiscus.library.plugin;

import me.mrgazdag.hibiscus.library.APIVersion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents information about the {@link Plugin} it was acquired from.
 * Information available:<ul>
 *     <li>{@link #getId() getting the Plugin's id}</li>
 *     <li>{@link #getName() getting the Plugin's name}</li>
 *     <li>{@link #getDescription() getting the Plugin's short description}</li>
 *     <li>{@link #getApiVersion() getting the Plugin's preferred API version}</li>
 *     <li>{@link #getMainClass() getting the Plugin's main class}</li>
 *     <li>{@link #getDependencies() getting the Plugin's dependencies}</li>
 *     <li>{@link #getSoftDependencies() getting the Plugin's soft-dependencies}</li>
 * </ul>
 */
public class PluginDescriptionFile {
    private final String id;
    private final String name;
    private final String description;
    private final String version;
    private final APIVersion apiVersion;
    private final String mainClass;
    private final String[] authors;
    private final String[] dependenciesStrings;
    private Plugin[] dependenciesResolved;
    private final String[] softDependenciesStrings;
    private Plugin[] softDependenciesResolved;

    /**
     * Creates a new {@link PluginDescriptionFile} from the specified parameters.
     * @param id the id of the plugin
     * @param name the name of the plugin
     * @param description the short description of the plugin
     * @param version the version of this plugin
     * @param apiVersion the api version this mod was made for
     * @param mainClass the path to the main class of this plugin
     * @param authors the authors of this plugin
     * @param dependenciesStrings the dependencies as strings
     * @param softDependenciesStrings the soft dependencies as strings
     */
    public PluginDescriptionFile(String id, String name, String description, String version, APIVersion apiVersion, String mainClass, String[] authors, String[] dependenciesStrings, String[] softDependenciesStrings) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.version = version;
        this.apiVersion = apiVersion;
        this.mainClass = mainClass;
        this.authors = authors;
        this.dependenciesStrings = dependenciesStrings;
        this.softDependenciesStrings = softDependenciesStrings;
    }

    /**
     * Creates a new {@link PluginDescriptionFile} from the specified {@link JSONObject}.
     * @param values the {@link JSONObject} containing data about the mod
     * @throws InvalidPluginDescriptionFileException if the specified JSONObject is missing required tags
     */
    public PluginDescriptionFile(JSONObject values) throws InvalidPluginDescriptionFileException {
        try {
            id = values.getString("id");
            name = values.getString("name");
            description = values.optString("description");
            version = values.getString("version");
            if (values.has("apiVersion")) apiVersion = APIVersion.fromString(values.getString("apiVersion"));
            else apiVersion = null;
            mainClass = values.getString("mainClass");
            if (values.optString("author", null) != null) {
                authors = new String[] {values.getString("author")};
            } else {
                JSONArray authorsArray = values.optJSONArray("authors");
                if (authorsArray == null) authors = new String[0];
                else {
                    authors = new String[authorsArray.length()];
                    for (int i = 0; i < authorsArray.length(); i++) authors[i] = authorsArray.getString(i);
                }
            }

            JSONArray dependenciesArray = values.optJSONArray("dependencies");
            if (dependenciesArray == null) dependenciesStrings = new String[0];
            else {
                dependenciesStrings = new String[dependenciesArray.length()];
                for (int i = 0; i < dependenciesArray.length(); i++) dependenciesStrings[i] = dependenciesArray.getString(i);
            }
            JSONArray softDependenciesArray = values.optJSONArray("softdependencies");
            if (softDependenciesArray == null) softDependenciesStrings = new String[0];
            else {
                softDependenciesStrings = new String[softDependenciesArray.length()];
                for (int i = 0; i < softDependenciesArray.length(); i++) softDependenciesStrings[i] = softDependenciesArray.getString(i);
            }
        } catch (JSONException e) {
            throw new InvalidPluginDescriptionFileException(e);
        }
    }

    /**
     * Returns the ID of the {@link Plugin}. Should be used in namespaces.
     * @return the ID of the {@link Plugin}
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the pretty name of the {@link Plugin}.
     * @return the name of the {@link Plugin}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a short description of the {@link Plugin}.
     * @return the description of the {@link Plugin}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the current version of the {@link Plugin}.
     * @return the version of the {@link Plugin}
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the fully qualifying name of the main {@link Class} of the {@link Plugin}.
     * (the one which extends {@link Plugin} itself)
     * @return the main {@link Class}
     */
    public String getMainClass() {
        return mainClass;
    }

    /**
     * Returns the API version that the {@link Plugin} was compiled against.
     * @return the API version
     */
    public APIVersion getApiVersion() {
        return apiVersion;
    }

    /**
     * Returns an array of the {@link Plugin}'s dependencies
     * @return the dependencies array
     */
    public Plugin[] getDependencies() {
        return dependenciesResolved;
    }

    /**
     * Returns an array of the {@link Plugin}'s soft-dependencies.
     * These mods can affect the behavior of the mods, such as<ul>
     *     <li>unlocking additional content</li>
     *     <li>tweaking</li>
     *     <li>resolving compatibility issues</li>
     * </ul>
     * @return the soft dependencies array
     */
    public Plugin[] getSoftDependencies() {
        return softDependenciesResolved;
    }

    String[] getDependenciesStrings() {
        return dependenciesStrings;
    }

    /**
     * Returns an array of the {@link Plugin}'s soft-dependencies' IDs.
     * These mods can affect the behavior of the mods, such as<ul>
     *     <li>unlocking additional content</li>
     *     <li>tweaking</li>
     *     <li>resolving compatibility issues</li>
     * </ul>
     * @return the soft dependencies as a {@link String }array
     */
    public String[] getAllSoftDependenciesStrings() {
        return softDependenciesStrings;
    }
    /**
     * Implementation-exclusive resolve call, resolves dependencies
     * @param mods a Map containing all mods
     * @return required but not found dependencies
     */
    String[] resolveDependencies(Map<String, Plugin> mods) {
        List<String> notFounds = new ArrayList<>();
        dependenciesResolved = new Plugin[dependenciesStrings.length];
        boolean bad = false;
        for (int i = 0; i < dependenciesStrings.length; i++) {
            String dependency = dependenciesStrings[i];
            Plugin mod = mods.get(dependency);
            if (mod == null) {
                notFounds.add(dependency);
                bad = true;
            } else {
                if (!bad) dependenciesResolved[i] = mod;
            }
        }
        softDependenciesResolved = new Plugin[softDependenciesStrings.length];
        for (int i = 0; i < softDependenciesStrings.length; i++) {
            String dependency = softDependenciesStrings[i];
            Plugin mod = mods.get(dependency);
            if (mod != null && !bad) softDependenciesResolved[i] = mod;
        }
        return notFounds.toArray(new String[0]);
    }
}
