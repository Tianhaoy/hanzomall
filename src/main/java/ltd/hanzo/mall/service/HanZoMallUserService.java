package ltd.hanzo.mall.service;


import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.entity.MallUser;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;

import javax.servlet.http.HttpSession;

public interface HanZoMallUserService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getHanZoMallUsersPage(PageQueryUtil pageUtil);

    /**
     * 用户注册
     *
     * @param loginName
     * @param password
     * @return
     */
    String register(String loginName, String password);

    /**
     * 登录
     *
     * @param loginName
     * @param passwordMD5
     * @param httpSession
     * @return
     */
    String login(String loginName, String passwordMD5, HttpSession httpSession);

    /**
     * 用户信息修改并返回最新的用户信息
     *
     * @param mallUser
     * @return
     */
    HanZoMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession);

    /**
     * 用户禁用与解除禁用(0-未锁定 1-已锁定)
     *
     * @param ids
     * @param lockStatus
     * @return
     */
    Boolean lockUsers(Integer[] ids, int lockStatus);

    /**
     * 用户密码修改并返回最新的用户信息
     *
     * @param mallUser
     * @return
     */
    HanZoMallUserVO updatePassword(MallUser mallUser, HttpSession httpSession);

    /**
     * 验证用户是否存在
     * @param loginName
     * @return
     */
    String getByLoginName(String loginName);

    /**
     * 验证码登录用户 验证码正确走这个登录
     * @param loginName
     * @return
     */
    String loginByLoginName(String loginName, HttpSession httpSession);
}
