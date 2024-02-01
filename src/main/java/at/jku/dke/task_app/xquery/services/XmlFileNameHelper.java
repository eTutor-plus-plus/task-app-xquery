package at.jku.dke.task_app.xquery.services;

import java.nio.file.Path;

/**
 * Helper for XML file names.
 */
public final class XmlFileNameHelper {
    private XmlFileNameHelper() {
    }

    /**
     * Gets the name of the diagnose XML file.
     *
     * @param id The id of the task group.
     * @return The name to the diagnose XML file.
     */
    public static String getDiagnoseFileName(long id) {
        return id + "-diagnose.xml";
    }


    /**
     * Gets the name of the submit XML file.
     *
     * @param id The id of the task group.
     * @return The name to the submit XML file.
     */
    public static String getSubmitFileName(long id) {
        return id + "-submit.xml";
    }

    /**
     * Gets the path to the diagnose XML file.
     *
     * @param directory The directory with the xml files.
     * @param id        The id of the task group.
     * @return The path to the diagnose XML file.
     */
    public static Path getDiagnoseFilePath(String directory, long id) {
        return Path.of(directory, getDiagnoseFileName(id)).normalize().toAbsolutePath();
    }

    /**
     * Gets the path to the submit XML file.
     *
     * @param directory The directory with the xml files.
     * @param id        The id of the task group.
     * @return The path to the submit XML file.
     */
    public static Path getSubmitFilePath(String directory, long id) {
        return Path.of(directory, getSubmitFileName(id)).normalize().toAbsolutePath();
    }
}
