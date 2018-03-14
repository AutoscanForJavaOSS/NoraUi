/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.service;

import org.openqa.selenium.support.ui.Select;

public interface UserNameService {

    /**
     * @param name
     * @param otherName
     * @return true or false
     */
    boolean comparingNames(String name, String otherName);

    /**
     * @param text
     *            search in dropDown
     * @param dropDown
     *            org.openqa.selenium.support.ui.Select element
     * @return id of option finded
     */
    int findOptionByIgnoreCaseText(String text, Select dropDown);

    /**
     * @param name
     *            input
     * @return normalized name
     */
    String getNormalizeName(String name);

}
