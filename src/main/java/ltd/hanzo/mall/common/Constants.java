package ltd.hanzo.mall.common;

/**
 * @author 皓宇QAQ
 * @qq交流群 951485783
 * @email 2469653218@qq.com
 * @link https://github.com/Tianhaoy/hanzomall
 * @apiNote 常量配置
 */
public class Constants {

    public final static String FILE_UPLOAD_DIC = "E:\\Iworkspace\\hanzo-mall\\upload\\";//上传文件的默认url前缀，根据部署设置自行修改
//    public final static String FILE_UPLOAD_DIC = "/home/work/hanzo-mall/upload/";//服务器路径

    public final static int INDEX_CAROUSEL_NUMBER = 5;//首页轮播图数量(可根据自身需求修改)

    public final static int INDEX_CATEGORY_NUMBER = 10;//首页一级分类的最大数量

    public final static int SEARCH_CATEGORY_NUMBER = 8;//搜索页一级分类的最大数量

    public final static int INDEX_GOODS_HOT_NUMBER = 4;//首页热卖商品数量
    public final static int INDEX_GOODS_NEW_NUMBER = 5;//首页新品数量
    public final static int INDEX_GOODS_RECOMMOND_NUMBER = 10;//首页推荐商品数量

    public final static int SHOPPING_CART_ITEM_TOTAL_NUMBER = 13;//购物车中商品的最大数量(可根据自身需求修改)

    public final static int SHOPPING_CART_ITEM_LIMIT_NUMBER = 5;//购物车中单个商品的最大购买数量(可根据自身需求修改)

    public final static String MALL_VERIFY_CODE_KEY = "mallVerifyCode";//验证码key

    public final static String MALL_USER_SESSION_KEY = "hanZoMallUser";//session中user的key

    public final static int GOODS_SEARCH_PAGE_LIMIT = 10;//搜索分页的默认条数(每页10条)

    public final static int ORDER_SEARCH_PAGE_LIMIT = 3;//我的订单列表分页的默认条数(每页3条)

    public final static int Bill_SEARCH_PAGE_LIMIT = 5;//我的账单列表分页的默认条数(每页5条)

    public final static int SELL_STATUS_UP = 0;//商品上架状态
    public final static int SELL_STATUS_DOWN = 1;//商品下架状态

    public final static String END_POINT ="http://oss-cn-beijing.aliyuncs.com/"; //阿里云oss信息
    public final static String ACCESS_KEY_ID  ="LTAI4FkKQn3SP7nnqmuL24ds";//访问阿里云API的验证
    public final static String ACCESS_KEY_SECRET ="Bd8MWzhAu2RgSsPlHxJsIRJE7GmmPN";//访问阿里云API的验证
    public final static String BUCKET_NAME = "hanzo-mall"; //仓库信息
    public final static String IMAGES_ADDRESS = "http://hanzoimage.babehome.com";//访问路径
    public final static String FILEDIR = "images/";//object路径

    public final static String APP_ID = "2016101700709698";//沙箱支付宝环境发起支付请求的应用ID
    //生成的应用私钥
    public final static String APP_PRIVATE_KEY ="";
    //支付宝公钥
    public final static String ALIPAY_PUBLIC_KEY = "";
    //这是沙箱接口路径,正式路径为https://openapi.alipay.com/gateway.do
    public final static String GATEWAY_URL ="https://openapi.alipaydev.com/gateway.do";//沙箱请求网关地址
    public final static String CHARSET = "UTF-8";// 编码
    public final static String FORMAT = "JSON";// 返回格式
    public final static String SIGN_TYPE = "RSA2";// 签名方式RSA2
    //支付宝服务器异步通知页面路径,付款完毕后会异步调用本项目的方法,必须为公网地址
    public final static String NOTIFY_URL = "http://mall.babehome.com:28089/alipay/alipayNotifyNotice";
    //支付宝同步通知路径,也就是当付款完毕后跳转本项目的页面,可以不是公网地址
//    public final static String RETURN_URL = "http://mall.babehome.com:28089/alipay/alipayReturnNotice";
    public final static String RETURN_URL = "http://localhost:28089/alipay/alipayReturnNotice";//本地回调
    public final static int ALIPAY_TYPE = 1;//支付宝支付类型为1 微信为2

    public final static String RANDOM_CODE = "randomCode";//邮箱随机验证码 key
}
