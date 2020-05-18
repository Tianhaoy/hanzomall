package ltd.hanzo.mall.controller.mall;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallShoppingCartItemVO;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.entity.HanZoMallShoppingCartItem;
import ltd.hanzo.mall.service.HanZoMallShoppingCartService;
import ltd.hanzo.mall.util.Result;
import ltd.hanzo.mall.util.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
@Api(tags = "ShoppingCartController", description = "购物车")
@Controller
public class ShoppingCartController {
    private final Logger log = LoggerFactory.getLogger(ShoppingCartController.class);
    @Resource
    private HanZoMallShoppingCartService hanZoMallShoppingCartService;

    @ApiOperation("购物车路由")
    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<HanZoMallShoppingCartItemVO> myShoppingCartItems = hanZoMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(HanZoMallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "error/error_5xx";
            }
            //总价
            for (HanZoMallShoppingCartItemVO hanZoMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += hanZoMallShoppingCartItemVO.getGoodsCount() * hanZoMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @ApiOperation("加入购物车")
    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveHanZoMallShoppingCartItem(@RequestBody HanZoMallShoppingCartItem hanZoMallShoppingCartItem,
                                                HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        hanZoMallShoppingCartItem.setUserId(user.getUserId());
        String saveResult = hanZoMallShoppingCartService.saveHanZoMallCartItem(hanZoMallShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @ApiOperation("修改购物车")
    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateHanZoMallShoppingCartItem(@RequestBody HanZoMallShoppingCartItem hanZoMallShoppingCartItem,
                                                   HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        hanZoMallShoppingCartItem.setUserId(user.getUserId());
        String updateResult = hanZoMallShoppingCartService.updateHanZoMallCartItem(hanZoMallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @ApiOperation("删除购物车")
    @DeleteMapping("/shop-cart/{hanZoMallShoppingCartItemId}")
    @ResponseBody
    public Result updateHanZoMallShoppingCartItem(@PathVariable("hanZoMallShoppingCartItemId") Long hanZoMallShoppingCartItemId,
                                                   HttpSession httpSession) {
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = hanZoMallShoppingCartService.deleteById(hanZoMallShoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @ApiOperation("结算购物车")
    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        HanZoMallUserVO user = (HanZoMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<HanZoMallShoppingCartItemVO> myShoppingCartItems = hanZoMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (HanZoMallShoppingCartItemVO hanZoMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += hanZoMallShoppingCartItemVO.getGoodsCount() * hanZoMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}
