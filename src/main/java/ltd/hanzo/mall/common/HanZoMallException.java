package ltd.hanzo.mall.common;

public class HanZoMallException extends RuntimeException {

    public HanZoMallException() {
    }

    public HanZoMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new HanZoMallException(message);
    }

}
