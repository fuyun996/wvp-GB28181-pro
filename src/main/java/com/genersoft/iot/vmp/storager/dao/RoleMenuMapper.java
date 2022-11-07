package com.genersoft.iot.vmp.storager.dao;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RoleMenuMapper {
    @Select("select count(*) from role_menu where role_id=#{roleId}")
    int countMenuByRoleId(int roleId);

    @Delete("delete from role_menu where role_id=#{roleId}")
    void deleteMenuByRoleId(int roleId);

    @Delete("delete from role_menu where menu_id=#{menuId}")
    int deleteMenuByMenuId(int menuId);

    @Insert("<script>" +
            "insert into role_menu(role_id, menu_id) values" +
            "<foreach collection=\"menuIds\" separator=\",\" item=\"item\">(#{roleId},#{item})</foreach>" +
    "</script>")
    void setMenuIdsByRole(@Param("menuIds") int[] menuIds, int roleId);
}
