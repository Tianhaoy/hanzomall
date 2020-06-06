package ltd.hanzo.mall.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author 皓宇QAQ
 * @Date 2020/6/5 21:42
 * @Description:搜索相关商品品牌名称，分类名称及属性
 */
@Getter
@Setter
public class EsProductRelatedInfo {
    private List<String> goodsNames;
    private List<String> goodsCategoryIds;
    private List<ProductAttr>   productAttrs;

    public static class ProductAttr{
        private Long attrId;
        private String attrName;
        private List<String> attrValues;

        public Long getAttrId() {
            return attrId;
        }

        public void setAttrId(Long attrId) {
            this.attrId = attrId;
        }

        public List<String> getAttrValues() {
            return attrValues;
        }

        public void setAttrValues(List<String> attrValues) {
            this.attrValues = attrValues;
        }

        public String getAttrName() {
            return attrName;
        }

        public void setAttrName(String attrName) {
            this.attrName = attrName;
        }
    }
}
