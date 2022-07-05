import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.api.UsersApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.auth.OAuth;
import io.swagger.client.model.AuthLoginBody;
import io.swagger.client.model.AuthSignupBody;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static final String MY_API_KEY = "5e0d5a706f7e5399ab0f5486fb91b06b2d40ab5c";
    public static ApiClient defaultClient;
    public static DefaultApi defaultApi;
    public static AuthApi authApi;
    public static UsersApi usersApi;
    public static User currentUser;
    public static long start = 0;
    public static String tracks;
    public static boolean isFirstCall = true;

    public static String profile;

    public static void main(String[] args) {
        authAPIKey();
        apiCheck();
        Scanner input = new Scanner(System.in);
        System.out.println("Welcome\n1-Login\n2-Signup");

        int choice = input.nextInt();
        boolean flag = false;
        switch (choice) {
            case 1: {
                while (!flag) {
                    flag = loginProcess();
                }
            }
            break;
            case 2: {
                while (!flag) {
                    flag = signupProcess();
                }
            }
            break;
        }
        flag = true;
        while (flag) {
            System.out.println("1-Profile\n2-Tracks\n3-Playlists\n4-Logout\n5-Exit");
            choice = input.nextInt();
            switch (choice) {
                case 1: {
                    profileProcess();
                }
                break;
                case 2: {
                    tracksProcess();
                }
                break;
                case 3: {
                    playlistsProcess();
                }
                break;
                case 4: {
                    logoutProcess();
                }
                break;
                case 5: {
                    flag = false;
                }
                break;
            }
        }

    }

    public static void authAPIKey() {
        defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
        ApiKeyAuth.setApiKey(MY_API_KEY);
        defaultApi = new DefaultApi();
        authApi = new AuthApi(defaultClient);
        usersApi = new UsersApi(defaultClient);
    }

    public static void apiCheck() {
        try {
            defaultApi.ping();
            if (defaultApi.reset().isSuccess()) {
                System.out.println("API is working");
            }
        } catch (ApiException apiException) {
            System.out.println(apiException.getResponseBody());
        }
    }

    public static boolean loginProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = input.next();
        System.out.println("Enter password:");
        String password = input.next();

        String token = "";
        try {
            AuthLoginBody authLoginBody = new AuthLoginBody();
            authLoginBody.setUsername(username);
            authLoginBody.setPassword(password);
            token = (authApi.login(authLoginBody).getToken());
            currentUser = new User(username, password);
            System.out.println("You successfully logged in");
            currentUser.token = token;
            defaultClient.setAccessToken(currentUser.token);
            OAuth bearerAuth = (OAuth) defaultClient.getAuthentication("bearerAuth");
            bearerAuth.setAccessToken(currentUser.token);
            currentUser = new User(username, password);
            currentUser.token = token;
            return true;
        } catch (ApiException apiException) {
            String errorResponse = apiException.getResponseBody();
            System.out.println(errorResponse);
            return false;
        }
    }

    public static boolean signupProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = input.next(), password = "";

        boolean flag = true;
        while (flag) {
            System.out.println("Enter password:");
            password = input.next();
            if (validatePassword(password) != null) {
                flag = false;
            } else {
                System.out.println("Password must be at least 8 characters long and contain at least one number, one uppercase and one lowercase letter");
            }
        }

        String token = "";
        try {
            AuthSignupBody authSignupBody = new AuthSignupBody();
            authSignupBody.setUsername(username);
            authSignupBody.setPassword(password);
            token = authApi.signUp(authSignupBody).getToken();
            currentUser = new User(username, password);
            System.out.println("You successfully signed up");
            currentUser.token = token;
            defaultClient.setAccessToken(currentUser.token);
            OAuth bearerAuth = (OAuth) defaultClient.getAuthentication("bearerAuth");
            bearerAuth.setAccessToken(currentUser.token);
            currentUser = new User(username, password);
            currentUser.token = token;
            return true;
        } catch (ApiException apiException) {
            System.out.println(apiException.getResponseBody());
            return false;
        }
    }

    public static String validatePassword(String value) {
        Pattern regex = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$");
        if (regex.matcher(value).matches()) {
            return value;
        } else {
            return null;
        }
    }


    public static void profileProcess() {
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - start > 20 || isFirstCall) {
            try {
                profile = usersApi.getProfileInfo().toString();
                System.out.println(profile);
                start = System.currentTimeMillis() / 1000;
                isFirstCall = false;
            } catch (ApiException apiException) {
                System.out.println(apiException.getResponseBody());
                isFirstCall = false;
            }
        } else {
            System.out.println(profile);
        }
    }

    public static void tracksProcess() {
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - start > 20 || isFirstCall) {
            try {
                tracks = usersApi.getTracksInfo().toString();
                System.out.println(tracks);
                start = System.currentTimeMillis() / 1000;
            } catch (ApiException apiException) {
                System.out.println(apiException.getResponseBody());
            }
        } else {
            System.out.println(tracks);
        }
    }

    public static void playlistsProcess() {

    }

    public static void logoutProcess() {

    }
}
