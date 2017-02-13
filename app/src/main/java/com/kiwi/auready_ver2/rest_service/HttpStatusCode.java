package com.kiwi.auready_ver2.rest_service;

/**
 * Http status code
 */

public final class HttpStatusCode {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public HttpStatusCode() {}

    /*
    * Friend
    * */
    public static class FriendStatusCode {
        public static final int OK = 200;
        public static final int NO_USERS = 204;
        public static final int DB_ERROR = 400;
    }
}
