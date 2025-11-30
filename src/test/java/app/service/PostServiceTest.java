package app.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import app.domain.Friend;
import app.domain.NewsFeed;
import app.domain.Post;
import app.domain.User;
import app.domain.UserPost;
import app.repository.*;
import app.repository.search.PostSearchRepository;
import app.service.impl.PostServiceImpl;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostSearchRepository postSearchRepository;

    @Mock
    private ReactiveElasticsearchOperations postSearchOperations;

    @Mock
    private NewsFeedRepository newsFeedRepository;

    @Mock
    private UserPostRepository userPostRepository;

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    private PostServiceImpl postService;

    @BeforeEach
    public void setup() {
        postService = new PostServiceImpl(
            postRepository,
            postSearchRepository,
            postSearchOperations,
            newsFeedRepository,
            userPostRepository,
            friendRepository,
            userRepository
        );
    }

    @Test
    void createPost_ShouldFanOutToFriends() {
        UUID userId = UUID.randomUUID();
        UUID friendId = UUID.randomUUID();
        Post post = new Post();
        post.setUserId(userId);
        post.setContent("Hello World");

        User user = new User();
        user.setId(userId.toString());
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAvatarUrl("avatar.jpg");

        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);

        when(postRepository.save(any(Post.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(postSearchRepository.save(any(Post.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(userPostRepository.save(any(UserPost.class))).thenReturn(Mono.just(new UserPost()));
        when(userRepository.findById(userId.toString())).thenReturn(Mono.just(user));
        when(friendRepository.findByUserId(userId)).thenReturn(Flux.just(friend));
        when(newsFeedRepository.save(any(NewsFeed.class))).thenReturn(Mono.just(new NewsFeed()));

        Mono<Post> result = postService.createPost(post);

        StepVerifier.create(result)
            .expectNextMatches(savedPost -> savedPost.getPostId() != null && savedPost.getContent().equals("Hello World"))
            .verifyComplete();

        verify(postRepository).save(any(Post.class));
        verify(userPostRepository).save(any(UserPost.class));
        verify(friendRepository).findByUserId(userId);
        verify(newsFeedRepository).save(any(NewsFeed.class)); // Should be called for the friend
    }
}
