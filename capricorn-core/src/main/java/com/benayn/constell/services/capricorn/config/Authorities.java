package com.benayn.constell.services.capricorn.config;

public class Authorities {

    public static final String ROLE_CAPRICORN = "ROLE_CAPRICORN";
    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    /* manage start */

    public static final String ACCOUNT_INDEX = "hasPermission(null, 'ACCOUNT_INDEX')";
    public static final String ACCOUNT_CREATE = "hasPermission(null, 'ACCOUNT_CREATE')";
    public static final String ACCOUNT_RETRIEVE = "hasPermission(null, 'ACCOUNT_RETRIEVE')";
    public static final String ACCOUNT_UPDATE = "hasPermission(null, 'ACCOUNT_UPDATE')";
    public static final String ACCOUNT_DELETE = "hasPermission(null, 'ACCOUNT_DELETE')";

    public static final String MODEL_ROLE_INDEX = "hasPermission(null, 'MODEL_ROLE_INDEX')";
    public static final String MODEL_ROLE_CREATE = "hasPermission(null, 'MODEL_ROLE_CREATE')";
    public static final String MODEL_ROLE_RETRIEVE = "hasPermission(null, 'MODEL_ROLE_RETRIEVE')";
    public static final String MODEL_ROLE_UPDATE = "hasPermission(null, 'MODEL_ROLE_UPDATE')";
    public static final String MODEL_ROLE_DELETE = "hasPermission(null, 'MODEL_ROLE_DELETE')";

    public static final String PERMISSION_INDEX = "hasPermission(null, 'PERMISSION_INDEX')";
    public static final String PERMISSION_CREATE = "hasPermission(null, 'PERMISSION_CREATE')";
    public static final String PERMISSION_RETRIEVE = "hasPermission(null, 'PERMISSION_RETRIEVE')";
    public static final String PERMISSION_UPDATE = "hasPermission(null, 'PERMISSION_UPDATE')";
    public static final String PERMISSION_DELETE = "hasPermission(null, 'PERMISSION_DELETE')";

    /* manage end */
}
