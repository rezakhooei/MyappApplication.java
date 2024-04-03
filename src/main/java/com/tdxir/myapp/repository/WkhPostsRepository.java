package com.tdxir.myapp.repository;

import com.tdxir.myapp.model.WkhPosts;
import com.tdxir.myapp.model.wkh_postmeta;
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

    public List<String> PostIdName (@Param("productName") String productName);
    // /var/www/tdx.ir/public_html
    @Query("SELECT  am.meta_value \n" +
            "FROM\n" +
            "    WkhPosts  p\n" +
            "LEFT JOIN\n" +
            "    wkh_postmeta pm ON\n" +
            "        pm.post_id = p.id AND\n" +
            "        pm.meta_key = '_thumbnail_id'\n" +
            "LEFT JOIN\n" +
            "    wkh_postmeta am ON\n" +
            "CAST(am.post_id as char) = CAST(pm.meta_value as char) AND \n"+

            "        am.meta_key = '_wp_attached_file'\n" +
            "WHERE\n" +
            "    p.post_type = 'product'\n" +
            "    AND p.post_status = 'publish'\n" +
            "    AND am.meta_value IS NOT NULL\n" +
            "and p.id=:id")
    public String imageUrl(@Param("id") String id);
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
