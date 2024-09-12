package dev.hemraj.jwtauthentication.Model.Blog;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Post")
@Data
public class Post {
    private String author_id;
    private String title;
    private String content;
    private String[] commments;
}
