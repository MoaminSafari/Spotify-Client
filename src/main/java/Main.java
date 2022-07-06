import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.api.AuthApi;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.api.PremiumUsersApi;
import io.swagger.client.api.UsersApi;
import io.swagger.client.auth.ApiKeyAuth;
import io.swagger.client.auth.OAuth;
import io.swagger.client.model.*;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    public static final String MY_API_KEY = "5e0d5a706f7e5399ab0f5486fb91b06b2d40ab5c";
    public static ApiClient defaultClient;
    public static DefaultApi defaultApi;
    public static AuthApi authApi;
    public static UsersApi usersApi;
    public static PremiumUsersApi premiumUsersApi;
    public static User currentUser;
    public static long start = 0;
    public static Tracks tracks;
    public static boolean isFirstCall = true;
    public static InlineResponse2003 profile;
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void main(String[] args) {
        authAPIKey();
        apiCheck();
        System.out.println("Welcome to the Spotify API!");
        MenuProcess();
    }

    public static void authAPIKey() {
        defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth ApiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("ApiKeyAuth");
        ApiKeyAuth.setApiKey(MY_API_KEY);
        defaultApi = new DefaultApi();
        authApi = new AuthApi(defaultClient);
        usersApi = new UsersApi(defaultClient);
        premiumUsersApi = new PremiumUsersApi(defaultClient);
    }

    public static void apiCheck() {
        try {
            defaultApi.ping();
            System.out.println("API is working");
        } catch (ApiException apiException) {
            System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
        }
    }

    public static void MenuProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("1-Login\n2-Signup");
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
                flag = false;
                while (!flag) {
                    flag = loginProcess();
                }
            }
                break;
        }
        flag = true;
        isFirstCall = true;
        profileProcess();
        isFirstCall = true;
        while (flag) {
            System.out.println("1-Profile\n2-Tracks\n3-Playlists\n4-Friends\n5-Upgrade\n6-Logout\n7-Exit");
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
                    friendsProcess();
                }
                    break;
                case 5: {
                    upgradeProcess();
                }
                    break;
                case 6: {
                    MenuProcess();
                    flag = false;
                }
                    break;
                case 7: {
                    flag = false;
                }
                    break;
            }
        }
    }

    public static boolean loginProcess() {
        Console console = System.console();
        Scanner input = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = input.next();
        String password = new String(console.readPassword("Enter password: "));

        String token;
        try {
            AuthLoginBody authLoginBody = new AuthLoginBody();
            authLoginBody.setUsername(username);
            authLoginBody.setPassword(password);
            token = (authApi.login(authLoginBody).getToken());
            currentUser = new User(username, password, token);
            System.out.println("You successfully logged in");
            defaultClient.setAccessToken(currentUser.token);
            OAuth bearerAuth = (OAuth) defaultClient.getAuthentication("bearerAuth");
            bearerAuth.setAccessToken(currentUser.token);
            return true;
        } catch (ApiException apiException) {
            String errorResponse = apiException.getResponseBody();
            System.out.println(ANSI_RED + errorResponse + ANSI_RESET);
            return false;
        }
    }

    public static boolean signupProcess() {
        Console console = System.console();
        Scanner input = new Scanner(System.in);
        System.out.println("Enter username:");
        String username = input.next(), password = "";

        boolean flag = true;
        while (flag) {
            password = new String(console.readPassword("Enter password: "));
            if (validatePassword(password) != null) {
                flag = false;
            } else {
                System.out.println(
                        "Password must be at least 8 characters long and contain at least one number, one uppercase and one lowercase letter");
            }
        }

        String token;
        try {
            AuthSignupBody authSignupBody = new AuthSignupBody();
            authSignupBody.setUsername(username);
            authSignupBody.setPassword(password);
            token = authApi.signUp(authSignupBody).getToken();
            currentUser = new User(username, password, token);
            System.out.println("You successfully signed up, now you can login");
            return true;
        } catch (ApiException apiException) {
            System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
            return false;
        }
    }

    public static String validatePassword(String value) {
        Pattern regex = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?\\d).{8,}$");
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
                profile = usersApi.getProfileInfo();
                System.out.println("Username: " + profile.getUsername());
                System.out.println("Premium Until: " + profile.getPremiumUntil());
                start = System.currentTimeMillis() / 1000;
            } catch (ApiException apiException) {
                System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
            }
            isFirstCall = false;
        } else {
            System.out.println("Username: " + profile.getUsername());
            System.out.println("Premium Until: " + profile.getPremiumUntil());
        }
    }

    public static void tracksProcess() {
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - start > 20 || isFirstCall) {
            try {
                tracks = usersApi.getTracksInfo();
                for (Track track : tracks) {
                    if (track.isIsPremium() == false
                            || (track.isIsPremium() == true && profile.getPremiumUntil() != null)) {
                        System.out.println("Name: " + track.getName());
                        System.out.println("Artist: " + track.getArtist());
                        System.out.println("ID: " + track.getId());
                        System.out.println();
                    }
                }
                start = System.currentTimeMillis() / 1000;
            } catch (ApiException apiException) {
                System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
            }
        } else {
            for (Track track : tracks) {
                if (track.isIsPremium() == false
                        || (track.isIsPremium() == true && profile.getPremiumUntil() != null)) {
                    System.out.println("Name: " + track.getName());
                    System.out.println("Artist: " + track.getArtist());
                    System.out.println("ID: " + track.getId());
                    System.out.println();
                }
            }
        }
    }

    public static void playlistsProcess() {
        Scanner input = new Scanner(System.in);
        boolean flag = true;
        while (flag) {
            System.out.println("1-Create playlist\n2-Get all playlists\n3-Exit");
            int choice = input.nextInt();
            switch (choice) {
                case 1: {
                    createPlaylistProcess();
                }
                    break;
                case 2: {
                    getAllPlaylistsProcess();
                }
                    break;
                case 3: {
                    flag = false;
                }
            }
        }

    }

    public static void createPlaylistProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter playlist name:");
        String playlistName = input.next();
        try {
            PlaylistsBody playlistsBody = new PlaylistsBody();
            playlistsBody.setName(playlistName);
            System.out.println("Playlist created successfully. ID: " + usersApi.createPlaylist(playlistsBody).getId());
        } catch (ApiException apiException) {
            System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
        }
    }

    public static void getAllPlaylistsProcess() {
        Scanner input = new Scanner(System.in);
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - start > 20 || isFirstCall) {
            try {
                currentUser.playlists = usersApi.getPlaylistsInfo();
                for (Playlist playlist : currentUser.playlists) {
                    System.out.println("Name: " + playlist.getName());
                    System.out.println("ID: " + playlist.getId());
                    System.out.println("Tracks: {");
                    for (Track track : playlist.getTracks()) {
                        System.out.println("Name: " + track.getName());
                        System.out.println("Artist: " + track.getArtist());
                        System.out.println("ID: " + track.getId());
                    }
                    System.out.println("}");
                }
                start = System.currentTimeMillis() / 1000;
            } catch (ApiException apiException) {
                System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
            }
            isFirstCall = false;
        } else {
            for (Playlist playlist : currentUser.playlists) {
                System.out.println("Name: " + playlist.getName());
                System.out.println("ID: " + playlist.getId());
                System.out.println("Tracks: {");
                for (Track track : playlist.getTracks()) {
                    System.out.println("Name: " + track.getName());
                    System.out.println("Artist: " + track.getArtist());
                    System.out.println("ID: " + track.getId());
                }
                System.out.println("}");
            }
        }

        boolean flag = true;
        while (flag) {
            System.out.println("1-Delete a playlist\n2-Add song to a playlist\n3-Delete song from a playlist\n4-Exit");
            int choice = input.nextInt();
            switch (choice) {
                case 1: {
                    deletePlaylistProcess();
                }
                    break;
                case 2: {
                    addSongToPlaylistProcess();
                }
                    break;
                case 3: {
                    deleteSongFromPlaylistProcess();
                }
                    break;
                case 4: {
                    flag = false;
                }
                    break;
            }
        }

    }

    public static void deletePlaylistProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter playlist's id:");
        int playlistId = input.nextInt();
        try {
            InlineResponse2001 response = usersApi.deletePlaylist(playlistId);
            if (response.isSuccess()) {
                System.out.println("Playlist deleted");
            }
        } catch (ApiException apiException) {
            System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
        }

    }

    public static void addSongToPlaylistProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter track's id:");
        String trackId = input.next();
        System.out.println("Enter playlist's id:");
        int playlistId = input.nextInt();
        try {
            InlineResponse2001 response = usersApi.addTrackToPlaylist(playlistId, trackId);
            if (response.isSuccess()) {
                System.out.println("Song added to playlist");
            }
        } catch (ApiException apiException) {
            System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
        }
    }

    public static void deleteSongFromPlaylistProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter track's id:");
        String trackId = input.next();
        System.out.println("Enter playlist's id:");
        int playlistId = input.nextInt();
        try {
            InlineResponse2001 response = usersApi.removeTrackFromPlaylist(playlistId, trackId);
            if (response.isSuccess()) {
                System.out.println("Song deleted from playlist");
            }
        } catch (ApiException apiException) {
            System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
        }
    }

    public static void friendsProcess() {
        if (profile.getPremiumUntil() == null) {
            System.out.println("You don't have premium account");
            return;
        }
        Scanner input = new Scanner(System.in);
        boolean flag = true;
        while (flag) {
            System.out.println("1-Get all friends\n2-Get friend requests\n3-Send friend request\n4-Exit");
            int choice = input.nextInt();
            switch (choice) {
                case 1: {
                    getAllFriendsProcess();
                }
                    break;
                case 2: {
                    getFriendRequestsProcess();
                }
                    break;
                case 3: {
                    sendFriendRequestProcess();
                }
                    break;
                case 4: {
                    flag = false;
                }
            }
        }
    }

    public static void getAllFriendsProcess() {
        Scanner input = new Scanner(System.in);
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - start > 20 || isFirstCall) {
            try {
                currentUser.friends = premiumUsersApi.getFriends();
                for (String friend : currentUser.friends) {
                    System.out.println("Name: " + friend);
                }
                start = System.currentTimeMillis() / 1000;
            } catch (ApiException apiException) {
                System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
            }
            isFirstCall = false;
        } else {
            for (String friend : currentUser.friends) {
                System.out.println("Name: " + friend);
            }
        }
        boolean flag = true;
        while (flag) {
            System.out.println("1-Get friend's playlists\n2-Exit");
            int choice = input.nextInt();
            switch (choice) {
                case 1: {
                    getFriendPlaylistsProcess();
                }
                    break;
                case 2: {
                    flag = false;
                }
            }
        }
    }

    public static void getFriendPlaylistsProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter friend's username:");
        String friendUsername = input.next();
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - start > 20 || isFirstCall) {
            try {
                currentUser.friendPlaylists = premiumUsersApi.getFriendPlaylists(friendUsername);
                for (Playlist playlist : currentUser.friendPlaylists) {
                    System.out.println("Name: " + playlist.getName());
                    System.out.println("ID: " + playlist.getId());
                    System.out.println("Tracks: {");
                    for (Track track : playlist.getTracks()) {
                        System.out.println("Name: " + track.getName());
                        System.out.println("Artist: " + track.getArtist());
                        System.out.println("ID: " + track.getId());
                    }
                    System.out.println("}");
                }
                start = System.currentTimeMillis() / 1000;
            } catch (ApiException apiException) {
                System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
            }
            isFirstCall = false;
        } else {
            for (Playlist playlist : currentUser.friendPlaylists) {
                System.out.println("Name: " + playlist.getName());
                System.out.println("ID: " + playlist.getId());
                System.out.println("Tracks: {");
                for (Track track : playlist.getTracks()) {
                    System.out.println("Name: " + track.getName());
                    System.out.println("Artist: " + track.getArtist());
                    System.out.println("ID: " + track.getId());
                }
                System.out.println("}");
            }
        }
    }

    public static void getFriendRequestsProcess() {
        Scanner input = new Scanner(System.in);
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - start > 20 || isFirstCall) {
            try {
                currentUser.friendRequests = premiumUsersApi.getFriendRequests();
                for (String request : currentUser.friendRequests) {
                    System.out.println("Name: " + request);
                }
                start = System.currentTimeMillis() / 1000;
            } catch (ApiException apiException) {
                System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
            }
            isFirstCall = false;
        } else {
            for (String request : currentUser.friendRequests) {
                System.out.println("Name: " + request);
            }
        }
        boolean flag = true;
        while (flag) {
            System.out.println("1-Accept friend request\n2-Exit");
            int choice = input.nextInt();
            switch (choice) {
                case 1: {
                    acceptFriendRequestProcess();
                }
                    break;
                case 2: {
                    flag = false;
                }
            }
        }
    }

    public static void acceptFriendRequestProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter User's username:");
        String username = input.next();
        try {
            InlineResponse2006 response = premiumUsersApi.addFriend(username);
            if (response.isSuccess()) {
                System.out.println("Friend request accepted");
            }
        } catch (ApiException apiException) {
            System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
        }
    }

    public static void sendFriendRequestProcess() {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter User's username:");
        String username = input.next();
        try {
            InlineResponse2006 response = premiumUsersApi.addFriend(username);
            if (response.isSuccess()) {
                System.out.println("Friend request sent");
            }
        } catch (ApiException apiException) {
            System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
        }
    }

    public static void upgradeProcess() {
        try {
            InlineResponse2005 response = usersApi.upgradeToPremium();
            if (response.isSuccess()) {
                System.out.println("Account upgraded successfully until: " + response.getPremiumUntil());
            }
            premiumUsersApi = new PremiumUsersApi(defaultClient);
        } catch (ApiException apiException) {
            System.out.println(ANSI_RED + apiException.getResponseBody() + ANSI_RESET);
        }
    }
}
