package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.RequestDto.CreateBlogDto;
import dev.hemraj.jwtauthentication.Service.BlogService.PostBlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/blog")
@RestController
@Slf4j
public class BlogController {
    private final PostBlogService postBlogService;
    public BlogController(PostBlogService postBlogService){
        this.postBlogService = postBlogService;
    }
    @PostMapping("/postblog")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createBlogController(@RequestHeader(name = "author-id") String author_id,@RequestBody CreateBlogDto data){
        try{
            return postBlogService.createBlog(data,author_id);
        }catch (Exception err){
            log.error("Error in creating the blog");
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Check the correct format of data");
    }
}
