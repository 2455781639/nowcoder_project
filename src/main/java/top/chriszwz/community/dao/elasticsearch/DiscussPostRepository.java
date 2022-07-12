package top.chriszwz.community.dao.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import top.chriszwz.community.entity.DiscussPost;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {


}