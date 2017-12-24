/**
 * NoraUi is licensed under the licence GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.data;

import com.github.noraui.exception.TechnicalException;

public interface DataOutputProvider extends DataProvider {

    /**
     * Writes a fail message in the output file.
     *
     * @param line
     *            the line to write the message on (0 is header, 1 and more are datas).
     * @param value
     *            the value to write
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    void writeFailedResult(int line, String value) throws TechnicalException;

    /**
     * Writes a warning message in the output file.
     *
     * @param line
     *            the line to write the message on (0 is header, 1 and more are datas).
     * @param value
     *            the value to write
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    void writeWarningResult(int line, String value) throws TechnicalException;

    /**
     * Writes a successful message in the output file.
     *
     * @param line
     *            the line to write the message on
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    void writeSuccessResult(int line) throws TechnicalException;

    /**
     * Writes a data in the output file.
     *
     * @param column
     *            the column to write the message on
     * @param line
     *            the line to write the message on (0 is header, 1 and more are datas).
     * @param value
     *            the value to write
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     */
    void writeDataResult(String column, int line, String value) throws TechnicalException;

}
