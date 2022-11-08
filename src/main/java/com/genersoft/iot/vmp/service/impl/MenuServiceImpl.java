package com.genersoft.iot.vmp.service.impl;

import com.genersoft.iot.vmp.service.IMenuService;
import com.genersoft.iot.vmp.storager.dao.MenuMapper;
import com.genersoft.iot.vmp.storager.dao.RoleMenuMapper;
import com.genersoft.iot.vmp.storager.dao.dto.Menu;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements IMenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;

    /**
     * 添加新菜单
     * @param menu 菜单对象
     * @return 1 操作成功 0 操作失败
     */
    @Override
    public int add(Menu menu) {
        return menuMapper.add(menu);
    }

    /**
     * 删除菜单
     * @param menuId 待删除菜单的id
     * @return 1 操作成功 0 操作失败
     */
    @Override
    @Transactional
    public int delete(int menuId) {
        int deleteMenu = menuMapper.delete(menuId);
        // 删除角色与此菜单关联的记录
        roleMenuMapper.deleteMenuByMenuId(menuId);
        return deleteMenu;
    }

    /**
     * 修改菜单信息
     * @param menu 菜单信息
     * @return 1 操作成功 0 操作失败
     */
    @Override
    public int updateById(Menu menu) {
        return menuMapper.update(menu);
    }

    /**
     * 更具角色获取其对应的菜单
     * @param roleId 角色id
     * @return 当前角色所拥有的菜单
     */
    @Override
    @Parameter(name = "roleId", description = "角色ID", required = true)
    public List<Menu> getMenuByRoleId(int roleId) {
        List<Menu> menus = menuMapper.getMenuByRoleId(roleId);
        return assembleMenu(menus);
    }

    /**
     * 查询系统的所有菜单
     * @return 系统的所有菜单
     */
    @Override
    public List<Menu> listAllMenus() {
        List<Menu> menus = menuMapper.listAllMenus();
        return assembleMenu(menus);
    }

    /**
     * 组装菜单的树级结构
     * @param menus 菜单集合
     * @return 数级结构菜单
     */
    private List<Menu> assembleMenu(List<Menu> menus) {

        ArrayList<Menu> assembledMenu = new ArrayList<>();

        for (Menu menu : menus) {
            // 判断是否是父节点
            if (menu.getPid() == 0) {
                // 寻找当前节点的自节点
                List<Menu> childNode = findChildNode(menu, menu.getId(), menus);
                menu.setChild(childNode);
                assembledMenu.add(menu);
            }
        }

        return menusSort(assembledMenu);
    }

    /**
     * 寻找指定菜单的子菜单
     * @param curMenu 需要寻找子菜单的菜单(父菜单)
     * @param curPid 子菜单说应属于的父菜单(父菜单id)
     * @param menus 全部菜单集合
     * @return curMenu的子菜单集合
     */
    private List<Menu> findChildNode(Menu curMenu, int curPid, List<Menu> menus) {
        ArrayList<Menu> childMenus = new ArrayList<>();

        for (Menu menu : menus) {
            // 判断当前节点的父节点是否是船传入的父节点
            if (menu.getPid() == curPid) {
                // 递归寻找此节点的子节点
                List<Menu> childMenu = findChildNode(menu, menu.getId(), menus);
                // 设置当前节点的子节点
                menu.setChild(childMenu);
                // 收集 curMenu 的子节点
                childMenus.add(menu);
            }
        }

        return childMenus.isEmpty() ? null : menusSort(childMenus);
    }

    /**
     * 对菜单进行排序，先以 sort 排序，sort相同时再以name排序
     * @param menus 待排序的菜单
     * @return 排序完成的菜单
     */
    private List<Menu> menusSort(List<Menu> menus) {
        return menus.stream().sorted((m1, m2) -> {
            // 先以 sort 排序，sort相同时再以name排序
            if (m1.getSort() < m2.getSort()) {
                return -1;
            } else if (m1.getSort() > m2.getSort()) {
                return 1;
            } else {
                return m2.getName().compareTo(m1.getName());
            }
        }).collect(Collectors.toList());
    }
}
