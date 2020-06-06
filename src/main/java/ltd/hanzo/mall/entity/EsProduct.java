package ltd.hanzo.mall.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author 皓宇QAQ
 * @Date 2020/6/4 20:49
 * @Description:搜索中的商品信息
 */
@Document(indexName = "hanzo", type = "product",shards = 1,replicas = 0)
@Data
public class EsProduct implements Serializable {
    private static final long serialVersionUID = -1L;
    @Id
    private Long goodsId;
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
    private String goodsName;
    @Field(analyzer = "ik_max_word",type = FieldType.Text)
    private String goodsIntro;

    private Long goodsCategoryId;

    private String goodsCoverImg;

    private String goodsCarousel;

    private Integer originalPrice;

    private Integer sellingPrice;

    private Integer stockNum;

    private String tag;

    private Byte goodsSellStatus;

    private Integer createUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Integer updateUser;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    @Field(type = FieldType.Keyword)
    private String goodsDetailContent;
}
