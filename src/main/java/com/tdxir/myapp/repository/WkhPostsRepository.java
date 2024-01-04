package com.tdxir.myapp.repository;

import com.tdxir.myapp.model.WkhPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WkhPostsRepository extends JpaRepository<WkhPosts, Long> {
    @Query("select MAX(w.id) from WkhPosts w  ")
    public Long lastId ();


    @Query("select w.post_title,w.id   from WkhPosts w where w.post_type='product' and w.post_title like :productName ")

    public List<String> PostId (@Param("productName") String productName);

    @Modifying
    @Transactional                 //   _price , _sale_price  , _regular_price , wcwp_wholesale
    @Query(value="update Wkh_Posts  SET post_title=:mahakName  WHERE  id=:meta_post_id", nativeQuery = true )

    //    ("UPDATE WkhPostMeta as w1 , (SELECT post_id  FROM WkhPostMeta WHERE meta_key='_sku' and meta_value='902') AS w2 SET w1.meta_value = '48' WHERE w1.post_id=w2.post_id")
    public void updateName(@Param("mahakName") String mahakName, @Param("meta_post_id") long meta_post_id );

    @Modifying
    @Transactional
    @Query(value = "insert into  wkh_posts  " +
            " (post_author,post_date,post_date_gmt,post_content,post_title,post_excerpt,post_status,comment_status,ping_status,post_password,post_name,to_ping ,pinged,post_modified,post_modified_gmt,post_content_filtered,post_type) values (1,'2023-12-24 18:27:03','2023-12-24 18:27:03','',:mahakName,'','publish','','','','','','','2023-12-24 18:27:03','2023-12-24 18:27:03','','product')",nativeQuery = true)
    public  void insertProduct(@Param("mahakName") String mahakName);



}
