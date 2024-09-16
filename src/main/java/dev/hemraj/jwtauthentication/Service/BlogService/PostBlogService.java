package dev.hemraj.jwtauthentication.Service.BlogService;

import dev.hemraj.jwtauthentication.Model.Blog.Comments;
import dev.hemraj.jwtauthentication.Model.Blog.Post;
import dev.hemraj.jwtauthentication.RequestDto.CreateBlogDto;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PostBlogService {

    @Autowired
    MongoTemplate mongoTemplate;

    public ResponseEntity<?> createBlog(CreateBlogDto blogPostData, String authorId) {
        try {
            Post blog = new Post();
            blog.setAuthor_id(authorId);
            blog.setTitle(blogPostData.getTitle());
            blog.setContent(blogPostData.getContent());

            ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            blog.setCreatedAt(zonedDateTime.format(dateTimeFormatter));

            if (blogPostData.getComments().isEmpty()) {
                blog.setComments(new ArrayList<>());
            } else {
                blog.setComments(blogPostData.getComments());
            }

            mongoTemplate.save(blog);

            return ResponseEntity.status(HttpStatus.OK).body("Data is been successfully saved!");
        } catch (Exception err) {
            log.error("error in saving data to db");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error in saving data!");
    }

    public ResponseEntity<?> addComments(Comments newComments,String id){
        ObjectId objectId = new ObjectId(id);
        try {
            Post blogData = mongoTemplate.findById(objectId, Post.class);
            if(blogData == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("blog data not found!");
            }

            Query query = new Query(Criteria.where("_id").is(objectId));
            Update update = new Update();
            if (newComments != null) {
                if (blogData.getComments().isEmpty()) {
                    update.set("comments",List.of(newComments));
                } else {
                    update.push("comments", newComments);
                }
                mongoTemplate.updateFirst(query, update, Post.class);

                return ResponseEntity.status(HttpStatus.OK).body("Comment added successfully!");
            }
        }catch (IllegalArgumentException err) {
            log.error("blog id not found : ", err);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid postID");
        }catch (Exception err){
            log.error("Exception in adding comment",err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error in adding comment to the blog");
    }

    // need to add the confirmation for deleting the data in frontend
    public ResponseEntity<?> deleteBlog(String id){
        try{
            Object blogID = new ObjectId(id);
            Post blogData = mongoTemplate.findById(blogID, Post.class);
            Query query = new Query(Criteria.where("_id").is(blogID));

            if(blogData == null) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The data is not found for the provided ID");
            }else{
                mongoTemplate.findAndRemove(query, Post.class);
                return ResponseEntity.status(HttpStatus.OK).body("The data is been deleted successfully for the ID provided");
            }
        }catch (IllegalArgumentException err){
            log.error("blogID not found");
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid postID");
        }catch (Exception err){
            log.error("Error in deleting the blog");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in deleting in the blog");
    }

    public ResponseEntity<?> getBlogs(){
        try {
            List<Post> getAllData = mongoTemplate.findAll(Post.class);
            return ResponseEntity.status(HttpStatus.OK).body(getAllData);
        }catch (Exception err){
            log.error("Exception in fetching data :" ,err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in fetching data");
    }

//    public ResponseEntity<?> editComment(String commentId, String alternateComment, String authorId,String userId,String blogID){
//        try {
//            Query query = new Query(Criteria.where("_id").is(blogID)
//                    .and("comments.id").is(commentId)
//                    .and("comments.commentAuthorId").is(userId));
//
//            Update update = new Update().set("comments.$.comments",alternateComment);
//            mongoTemplate.updateFirst(query, update, Post.class);
//        }catch (Exception err){
//            log.error("Error in editing the comment  :", err);
//        }
//    }


}
