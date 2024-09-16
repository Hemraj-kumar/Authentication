package dev.hemraj.jwtauthentication.Model.Blog;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document("Post")
@Data
public class Post {
    private String author_id;
    private String title;
    private String content;
    private List<Comments> comments;
    private String createdAt;
    public Post(){};
}
