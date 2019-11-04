package com.cross.sync.provider;

import com.cross.sync.exception.ProviderException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface LinuxProvider {
    /**
     * Returns an input stream representing the requested document.
     * Throws an exception if there is no file or connection problem
     *
     * @param path path to file
     * @return RemoteInputStream
     * @throws ProviderException provider exception
     */
    InputStream loadFile(String path) throws ProviderException;

    /**
     * Returns an output stream to upload the response document
     * Throws an exception if there is connection problem
     *
     * @param path path to file
     * @return RemoteOutputStream
     * @throws ProviderException provider exception
     */
    OutputStream uploadFile(String path) throws ProviderException;

    /**
     * Return file's mtime
     * Throws an exception if there is connection problem
     *
     * @param path path to file
     * @return Long last modification time
     * @throws ProviderException provider exception
     */
    Long getMTime(String path) throws ProviderException;

    /**
     * Check connection
     *
     * @return Boolean
     */
    Boolean ping();


    /**
     * Create file
     *
     * @param path path to file
     * @throws ProviderException provider exception
     */
    void createFile(String path) throws ProviderException;

    /**
     * Create directory
     *
     * @param path path to directory
     * @throws ProviderException provider exception
     */
    void createDirectory(String path) throws ProviderException;

    /**
     * Delete file
     *
     * @param path path to file
     * @throws ProviderException provider exception
     */
    void deleteFile(String path) throws ProviderException;

    /**
     * Move file `from` to `to`
     *
     * @param from from path to file
     * @param to   to path to file
     * @throws ProviderException provider exception
     */
    void moveFile(String from, String to) throws ProviderException;


    /**
     * Check file existence
     *
     * @param path path to file
     * @return true if exist and false otherwise
     */
    Boolean existFile(String path);

    /**
     * Check for file read privileges
     *
     * @param path path to file
     * @return true if can read false otherwise
     */
    Boolean canRead(String path);

    /**
     * Check for file write privileges
     *
     * @param path path to file
     * @return true if can write and false otherwise
     */
    Boolean canWrite(String path);

    /**
     * Check file type
     *
     * @param path path to file
     * @return true if directory, false otherwise
     * @throws ProviderException provider exception
     */
    Boolean isDirectory(String path) throws ProviderException;

    /**
     * Return list of files, that contains in path folder. Throw exception if it isn't a folder;
     *
     * @param path to folder
     * @return list of files
     * @throws ProviderException provider exception
     */
    List<String> getFileList(String path) throws ProviderException;


    /**
     * Get size of file
     *
     * @param path path to file or directory
     * @return size
     */
    Long getSize(String path) throws ProviderException;

    @SuppressWarnings("RegExpRedundantEscape")
    default Boolean validatePath(String path) {
        Pattern pathTemplate = Pattern.compile("\\/([\\.A-z0-9-_+]+\\/)*[\\.A-z0-9-_+]*");
        Matcher m = pathTemplate.matcher(path);
        return m.matches();
    }
}
