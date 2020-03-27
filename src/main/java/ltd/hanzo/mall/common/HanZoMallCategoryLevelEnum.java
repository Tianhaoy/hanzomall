package ltd.hanzo.mall.common;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 * @apiNote 分类级别
 */
public enum HanZoMallCategoryLevelEnum {

    DEFAULT(0, "ERROR"),
    LEVEL_ONE(1, "一级分类"),
    LEVEL_TWO(2, "二级分类"),
    LEVEL_THREE(3, "三级分类");

    private int level;

    private String name;

    HanZoMallCategoryLevelEnum(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public static HanZoMallCategoryLevelEnum getHanZoMallOrderStatusEnumByLevel(int level) {
        for (HanZoMallCategoryLevelEnum hanzoMallCategoryLevelEnum : HanZoMallCategoryLevelEnum.values()) {
            if (hanzoMallCategoryLevelEnum.getLevel() == level) {
                return hanzoMallCategoryLevelEnum;
            }
        }
        return DEFAULT;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
