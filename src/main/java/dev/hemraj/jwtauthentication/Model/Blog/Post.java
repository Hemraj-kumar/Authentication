package dev.hemraj.jwtauthentication.Model.Blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document("Post")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private String author_id;
    private String title;
    private String content;
    private List<Comments> comments;
    private String createdAt;
}
