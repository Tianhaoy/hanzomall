package ltd.hanzo.mall.service.impl;

import lombok.extern.slf4j.Slf4j;

import ltd.hanzo.mall.common.Constants;
import ltd.hanzo.mall.common.ServiceResultEnum;
import ltd.hanzo.mall.controller.vo.HanZoMallUserVO;
import ltd.hanzo.mall.dao.MallUserMapper;
import ltd.hanzo.mall.entity.MallUser;
import ltd.hanzo.mall.service.HanZoMallUserService;
import ltd.hanzo.mall.util.BeanUtil;
import ltd.hanzo.mall.util.MD5Util;
import ltd.hanzo.mall.util.PageQueryUtil;
import ltd.hanzo.mall.util.PageResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Service
public class HanZoMallUserServiceImpl implements HanZoMallUserService {

    @Resource
    private MallUserMapper mallUserMapper;

    @Override
    public PageResult getHanZoMallUsersPage(PageQueryUtil pageUtil) {
        List<MallUser> mallUsers = mallUserMapper.findMallUserList(pageUtil);
        int total = mallUserMapper.getTotalMallUsers(pageUtil);
        PageResult pageResult = new PageResult(mallUsers, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String register(String loginName, String password) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SAME_LOGIN_NAME_EXIST.getResult();
        }
        MallUser registerUser = new MallUser();
        registerUser.setLoginName(loginName);
        registerUser.setNickName(loginName);
        String passwordMD5 = MD5Util.MD5Encode(password, "UTF-8");
        registerUser.setPasswordMd5(passwordMD5);
        if (mallUserMapper.insertSelective(registerUser) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String login(String loginName, String passwordMD5, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByLoginNameAndPasswd(loginName, passwordMD5);
        if (user != null && httpSession != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            HanZoMallUserVO hanZoMallUserVO = new HanZoMallUserVO();
            BeanUtil.copyProperties(user, hanZoMallUserVO);
            //设置购物车中的数量
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, hanZoMallUserVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }

    @Override
    public HanZoMallUserVO updateUserInfo(MallUser mallUser, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
        if (user != null) {
            user.setNickName(mallUser.getNickName());
            user.setAddress(mallUser.getAddress());
            user.setEmailAddress(mallUser.getEmailAddress());
            user.setIntroduceSign(mallUser.getIntroduceSign());
            if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
                HanZoMallUserVO hanZoMallUserVO = new HanZoMallUserVO();
                user = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(user, hanZoMallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, hanZoMallUserVO);
                return hanZoMallUserVO;
            }
        }
        return null;
    }

    @Override
    public Boolean lockUsers(Integer[] ids, int lockStatus) {
        if (ids.length < 1) {
            return false;
        }
        return mallUserMapper.lockUserBatch(ids, lockStatus) > 0;
    }

    @Override
    public HanZoMallUserVO updatePassword(MallUser mallUser, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
        String passwordMD5 = MD5Util.MD5Encode(mallUser.getPasswordMd5(), "UTF-8");
        if (user != null) {
            user.setPasswordMd5(passwordMD5);
            if (mallUserMapper.updateByPrimaryKeySelective(user) > 0) {
                HanZoMallUserVO hanZoMallUserVO = new HanZoMallUserVO();
                user = mallUserMapper.selectByPrimaryKey(mallUser.getUserId());
                BeanUtil.copyProperties(user, hanZoMallUserVO);
                httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, hanZoMallUserVO);
                return hanZoMallUserVO;
            }
        }
        return null;
    }

    @Override
    public String getByLoginName(String loginName) {
        if (mallUserMapper.selectByLoginName(loginName) != null) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return null;
    }

    /**
     * 验证码正确只需要走这个登录
     * @param loginName
     * @param httpSession
     * @return
     */
    @Override
    public String loginByLoginName(String loginName, HttpSession httpSession) {
        MallUser user = mallUserMapper.selectByLoginName(loginName);
        if (user != null && httpSession != null) {
            if (user.getLockedFlag() == 1) {
                return ServiceResultEnum.LOGIN_USER_LOCKED.getResult();
            }
            //昵称太长 影响页面展示
            if (user.getNickName() != null && user.getNickName().length() > 7) {
                String tempNickName = user.getNickName().substring(0, 7) + "..";
                user.setNickName(tempNickName);
            }
            HanZoMallUserVO hanZoMallUserVO = new HanZoMallUserVO();
            BeanUtil.copyProperties(user, hanZoMallUserVO);
            //设置购物车中的数量
            httpSession.setAttribute(Constants.MALL_USER_SESSION_KEY, hanZoMallUserVO);
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.LOGIN_ERROR.getResult();
    }


}
