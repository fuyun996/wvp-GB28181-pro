package com.genersoft.iot.vmp.storager.dao;

import com.genersoft.iot.vmp.storager.dao.dto.Menu;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MenuMapper {

    @Insert("insert into menu(pid, name, url, sort, spread, status, createTime, updateTime,icon) " +
            "values(#{pid}, #{name}, #{url}, #{sort}, #{spread}, #{status}, #{createTime}, #{updateTime},#{icon})")
    int add(Menu menu);

    @Delete("delete from menu where id=#{menuId}")
    int delete(int menuId);

    @Update("<script>" + "update menu " +
                "<set>" +
                    "<if test=\"pid != null\">pid=#{pid},</if>" +
                    "<if test=\"name != null\">name=#{name},</if>" +
                    "<if test=\"url != null\">url=#{url},</if>" +
                    "<if test=\"sort != null\">sort=#{sort},</if>" +
                    "<if test=\"spread != null\">spread=#{spread},</if>" +
                    "<if test=\"status != null\">status=#{status},</if>" +
                    "<if test=\"updateTime != null\">updateTime=#{updateTime},</if>" +
                    "<if test=\"icon != null\">icon=#{icon},</if>" +
                "</set> where id=#{id}" +
            "</script>")
    int update(Menu menu);

    @Select("<script>" + "select m.id, m.pid, m.name, m.url, m.sort, m.spread,m.icon from menu m where m.status = 1 " +
                "<if test=\"roleId != 1\">" +
                    "and m.id in (select rm.menu_id from role_menu rm where rm.role_id=#{roleId})" +
                "</if>" +
                "order by m.sort" +
            "</script>")
    List<Menu> getMenuByRoleId(int roleId);

    @Select("select id, pid, name, url, sort, spread,icon from menu where status = 1")
    List<Menu> listAllMenus();
}
