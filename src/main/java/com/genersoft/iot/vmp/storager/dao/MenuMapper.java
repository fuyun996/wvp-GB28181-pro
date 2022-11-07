package com.genersoft.iot.vmp.storager.dao;

import com.genersoft.iot.vmp.storager.dao.dto.Menu;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MenuMapper {

    @Insert("insert into menu(name, url, status, createTime, updateTime) " +
            "values(#{name}, #{url}, #{status}, #{createTime}, #{updateTime})")
    int add(Menu menu);

    @Delete("delete from menu where id=#{menuId}")
    int delete(int menuId);

    @Select("select m.id, m.name, m.url from menu m where m.status = 1 and m.id in (select rm.menu_id from role_menu rm where rm.role_id=#{roleId})")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "url",column = "url")
    })
    List<Menu> getMenuByRoleId(int roleId);

    @Select("select id, name, url from menu")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "url",column = "url")
    })
    List<Menu> listAllMenus();
}
