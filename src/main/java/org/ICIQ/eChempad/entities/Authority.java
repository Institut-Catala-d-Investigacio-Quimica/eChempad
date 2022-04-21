/*
 * |===================================================================================|
 * | Copyright (C) 2021 - 2022 ICIQ <contact@iochem-bd.org>                            |
 * |                                                                                   |
 * | This software is the property of ICIQ.                                            |
 * |===================================================================================|
 */
package org.ICIQ.eChempad.entities;


/**
 * Enumerate object to store the different privileges that a user can have against arbitrary resources such as an
 * Experiment or Journal, in a comprehensive manner. To access any resource a user must be authenticated with a role.
 * When calling a REST API method, the user will be authenticated to know if it can use the application. If it is, we
 * will retrieve its authorities to know if that user is allowed to "use" the resource in the requested manner.
 */
public enum Authority {
    // The position of each value can be retrieved using Authority.ordinal()
    NONE,  // 0
    READ,  // 1
    WRITE,  // 2
    EDIT,  // 3
    OWN,  // 4
}




