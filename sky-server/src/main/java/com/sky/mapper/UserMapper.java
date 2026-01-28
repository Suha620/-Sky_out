package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据分类id查询菜品数量
     * @param OpenId
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String OpenId );

    @Select("select * from user where id =#{id}")
    User getById(Long id);

    void insert(User user);

    @Select("select count(id) from user;")
    Integer getAllUser();

    Integer countUser(Map<String, Object> map);
    /**
     * 根据动态条件统计用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
