package com.cross.sync.provider;

import com.cross.sync.exception.ProviderException;

import java.io.InputStream;
import java.io.OutputStream;

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
     * Return file's md5 hash
     * Throws an exception if there is connection problem
     *
     * @param path path to file
     * @return String
     * @throws ProviderException provider exception
     */
    String getMD5FileHash(String path) throws ProviderException;

    /**
     * Check connection
     *
     * @return Boolean
     * @throws ProviderException provider exception
     */
    Boolean ping() throws ProviderException;


    /**
     * Create file
     *
     * @param path path to file
     * @throws ProviderException provider exception
     */
    void createFile(String path) throws ProviderException;

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
     * @throws ProviderException provider exception
     */
    Boolean existFile(String path) throws ProviderException;
}
