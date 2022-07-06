import io.swagger.client.model.Playlists;

import java.util.List;

public class User {
    public String username;
    public String password;
    public Playlists playlists;
    public String token;
    public List<String> friends;
    public List<String> friendRequests;
    public Playlists friendPlaylists;

    public User(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
    }
}
