package com.hungrymachine.hungrydroid.api;

import com.hungrymachine.hungrydroid.HungryDroid;
import com.hungrymachine.hungrydroid.R;
import com.hungrymachine.hungrydroid.net.NetworkService;
import com.hungrymachine.hungrydroid.utils.HungryLogger;


public abstract class ApiException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static final String NO_AUTH = "Received authentication challenge is null";
    public static final String SOCKET_TIMEOUT_EXCEPTION = "SocketTimeoutException";
    private static final String name = "ApiException";

    public ApiException() {
    }

    /**
     * Return text appropriate for presenting to user, for instance in a dialog or alert.
     *
     * @return
     */
    public abstract String getErrorMessageForUser();

    public ApiException(Exception ex) {
        super(ex);
    }

    public ApiException(String errorMessage) {
        super(new RuntimeException(errorMessage));
    }

    /**
     * Returns true if this Exception was related to network unavailability, such as a timeout
     * or, well, network unavailability.
     *
     * @return
     */
    public boolean isConnectivityException() {
        return false;
    }

    /**
     * Returns true if credentials (BasicAuth, Facebook, etc.) were rejected.
     *
     * @return
     */
    public boolean isAuthenticationException() {
        return false;
    }

    public static class TimeOut extends ApiException {
        private static final long serialVersionUID = 2L;
        private final static String errorMessage = getString(R.string.hd_server_timeout_message);

        public TimeOut(Exception ex) {
            super(ex);
        }

        @Override
        public String getErrorMessageForUser() {
            return errorMessage;
        }

        @Override
        public boolean isConnectivityException() {
            return true;
        }
    }

    public static class NotAuthenticated extends ApiException {
        private static final long serialVersionUID = 3L;
        private final static String errorMessage = getString(R.string.hd_authentication_failed_message);

        public NotAuthenticated(Exception ex) {
            super(ex);
        }

        @Override
        public String getErrorMessageForUser() {
            return errorMessage;
        }

        @Override
        public boolean isAuthenticationException() {
            return true;
        }
    }

    public static class ConnectionUnavailable extends ApiException {
        private static final long serialVersionUID = 4L;
        private final static String errorMessage = getString(R.string.hd_connection_unavailable);

        public ConnectionUnavailable(Exception ex) {
            super(ex);
        }

        public ConnectionUnavailable() {
        }

        @Override
        public String getErrorMessageForUser() {
            return errorMessage;
        }

        @Override
        public boolean isConnectivityException() {
            return true;
        }
    }

    private static String getString(int stringId) {
        return HungryDroid.appInstance().getString(stringId);
    }


    public static class ServerError extends ApiException {
        private static final long serialVersionUID = 5L;
        private final static String errorMessage = getString(R.string.hd_server_error_message);

        public ServerError(Exception ex) {
            super(ex);
        }

        public ServerError() {
            super(new RuntimeException("No exception given in constructor"));
        }

        @Override
        public String getErrorMessageForUser() {
            return errorMessage;
        }
    }

    /**
     * A factory method for creating different ApiException types for the given exception/message
     *
     * @param ex
     * @return
     */
    public static ApiException create(Exception ex) {
        if (ex != null) {
            if (ex.getMessage() != null && ex.getMessage().contains(NetworkService.NO_NETWORK_AVAILABLE)) {
                return new ConnectionUnavailable(ex);
            } else if (ex.getMessage() != null && (ex.getMessage().contains(NO_AUTH))) {
                return new NotAuthenticated(ex);
            } else if (ex.getMessage() != null && (ex.getMessage().contains(SOCKET_TIMEOUT_EXCEPTION))) {
                return new TimeOut(ex);
            } else {
                return new ServerError(ex);
            }
        } else {
            HungryLogger.e(name, "Null exception given to ApiException builder");
            return new ServerError();
        }
    }

}
