package top.chriszwz.community;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.kafka.core.KafkaTemplate;
import top.chriszwz.community.dao.DiscussPostMapper;
import top.chriszwz.community.dao.UserMapper;
import top.chriszwz.community.dao.elasticsearch.DiscussPostRepository;
import top.chriszwz.community.entity.DiscussPost;
import top.chriszwz.community.util.MailClient;
import top.chriszwz.community.util.SensitiveFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class CommunityApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private MailClient mailClient;



    @Test
    void testSearch() {
//        discussPostRepository.save(discussPostMapper.selectDiscussPostById(276));
//        discussPostRepository.save(discussPostMapper.selectDiscussPostById(275));
//        discussPostRepository.save(discussPostMapper.selectDiscussPostById(274));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(0, 0, 10));
    }

    @Test
    void testSearchByTemplate(){
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("offer","title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("create_time").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        Page<DiscussPost> page = elasticsearchTemplate.queryForPage(searchQuery, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                if(hits.getTotalHits() <= 0){
                    return null;
                }

                List<DiscussPost> list = new ArrayList<>();
                for(SearchHit hit: hits){
                    DiscussPost post = new DiscussPost();

                    String id = hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.parseInt(id));

                    String userId = hit.getSourceAsMap().get("user_id").toString();
                    post.setUser_id(Integer.parseInt(userId));

                    String title = hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);

                    String content = hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);

                    String status = hit.getSourceAsMap().get("status").toString();
                    post.setStatus(Integer.parseInt(status));

                    String createTime = hit.getSourceAsMap().get("create_time").toString();
                    post.setCreate_time(new Date(Long.parseLong(createTime)));

                    String commentCount = hit.getSourceAsMap().get("comment_count").toString();
                    post.setComment_count(Integer.parseInt(commentCount));

                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if(titleField != null){
                        String titleHighlight = titleField.getFragments()[0].toString();
                        post.setTitle(titleHighlight);
                    }

                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if(contentField != null){
                        String contentHighlight = contentField.getFragments()[0].toString();
                        post.setTitle(contentHighlight);
                    }
                    list.add(post);

                }
                return new AggregatedPageImpl(list, pageable,
                        hits.getTotalHits(), searchResponse.getAggregations(), searchResponse.getScrollId(), hits.getMaxScore());
            }
        });
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for(DiscussPost post : page) {
            System.out.println(post.toString());
        }
    }



    @Test
    public void mailTest(){
        mailClient.sendMail("zwz2455781639@gmail.com","test","???????????????Chris??????????????????????????????");
    }

}
