package com.tdxir.myapp.repository;

import com.tdxir.myapp.model.wkh_postmeta;
import kotlin.jvm.Throws;
import org.hibernate.annotations.SecondaryRows;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

public interface WkhPostMetaRepository extends JpaRepository<wkh_postmeta,Long> {



    @Query("select w1.post_id from wkh_postmeta w1 where w1.meta_key='_sku' and w1.meta_value=:mahakCode")

        public long post_id (@Param("mahakCode") String mahakCode);

    @Modifying
    @Transactional
    @Query(value="update wkh_postmeta  SET meta_value=:mahakStock  WHERE  meta_key='_stock' and  (post_id =:mahakCode )", nativeQuery = true )

        //    ("UPDATE WkhPostMeta as w1 , (SELECT post_id  FROM WkhPostMeta WHERE meta_key='_sku' and meta_value='902') AS w2 SET w1.meta_value = '48' WHERE w1.post_id=w2.post_id")
    public void updateStock(@Param("mahakStock") String mahakStock,@Param("mahakCode") String mahakCode );
    @Modifying
    @Transactional                 //   _price , _sale_price  , _regular_price , wcwp_wholesale
    @Query(value="update wkh_postmeta  SET meta_value=:mahakPrice  WHERE  meta_key='_price' and  (post_id =:mahakCode )", nativeQuery = true )

    //    ("UPDATE WkhPostMeta as w1 , (SELECT post_id  FROM WkhPostMeta WHERE meta_key='_sku' and meta_value='902') AS w2 SET w1.meta_value = '48' WHERE w1.post_id=w2.post_id")
    public void updatePrice(@Param("mahakPrice") String mahakPrice,@Param("mahakCode") String mahakCode );
    @Modifying
    @Transactional                 //   _price , _sale_price  , _regular_price , wcwp_wholesale
    @Query(value="insert into Wkh_postmeta (post_id,meta_key,meta_value) values (:post_id,'_sku',:mahakCode) ",nativeQuery = true)

    public void insertSku(@Param("post_id") Long postid,@Param("mahakCode") String mahakCode);

    @Modifying
    @Transactional                 //   _price , _sale_price  , _regular_price , wcwp_wholesale
    @Query(value=" insert into Wkh_postmeta (post_id,meta_key,meta_value) values (:post_id,'_stock',:mahakStock)",nativeQuery = true)
    public void insertStock(@Param("post_id") Long postid,@Param("mahakStock") String mahakStock);
    @Modifying
    @Transactional                 //   _price , _sale_price  , _regular_price , wcwp_wholesale
    @Query(value="insert into Wkh_postmeta (post_id,meta_key,meta_value) values (:post_id,'_price',:mahakPrice)",nativeQuery = true)
    public void insertPrice(@Param("post_id") Long postid,@Param("mahakPrice") String mahakPrice);

}
