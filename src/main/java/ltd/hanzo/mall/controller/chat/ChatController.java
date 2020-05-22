package ltd.hanzo.mall.controller.chat;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author 皓宇QAQ
 * @email 2469653218@qq.com
 * @Date 2020/4/17 17:34
 * @link https://github.com/Tianhaoy/hanzomall
 * @Description:
 */
@Api(tags = "ChatController", description = "客服聊天")
@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {

    @ApiOperation("聊天测试")
    @RequestMapping({"/test"})
    public String test() {
        log.info("聊天test成功！");
        return "chat/testlayim";
    }
}
