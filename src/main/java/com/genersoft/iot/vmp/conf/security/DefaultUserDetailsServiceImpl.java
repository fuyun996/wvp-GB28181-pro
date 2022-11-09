package com.genersoft.iot.vmp.conf.security;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.genersoft.iot.vmp.service.IMenuService;
import com.genersoft.iot.vmp.service.IRoleDeviceChannelService;
import com.genersoft.iot.vmp.service.IRoleMenuService;
import com.genersoft.iot.vmp.storager.dao.MenuMapper;
import com.genersoft.iot.vmp.storager.dao.dto.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.StringUtils;
import com.genersoft.iot.vmp.conf.security.dto.LoginUser;
import com.genersoft.iot.vmp.service.IUserService;
import com.genersoft.iot.vmp.storager.dao.dto.User;

/**
 * 用户登录认证逻辑
 */
@Component
public class DefaultUserDetailsServiceImpl implements UserDetailsService {

    private final static Logger logger = LoggerFactory.getLogger(DefaultUserDetailsServiceImpl.class);

    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleDeviceChannelService roleDeviceChannelService;

    @Autowired
    private IRoleMenuService roleMenuService;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            logger.info("登录用户：{} 不存在", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        }

        // 查出密码
        User user = userService.getUserByUsername(username);
        if (user == null) {
            logger.info("登录用户：{} 不存在", username);
            throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        }
        String password = SecurityUtils.encryptPassword(user.getPassword());
        user.setPassword(password);

        // 查询用所具有的权限
        List<String> authorities = roleDeviceChannelService.getDeviceChannelByRoleId(user.getRole().getId());
        // 获取菜单权限
        List<String> menuAuthorities = menuMapper.getMenuByRoleId(user.getRole().getId()).stream().map(Menu::getUrl).collect(Collectors.toList());
        // 合并权限
        authorities.addAll(menuAuthorities);
        // 封装
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(authorities.toArray(new String[0]));
        // 添加用户角色信息，以角色信息作为一种权限
        authorityList.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));

        LoginUser loginUser = new LoginUser(user, LocalDateTime.now());
        loginUser.setAuthorities(authorityList);
        return loginUser;
    }


}
