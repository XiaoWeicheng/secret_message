package org.example.secret_message.user;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @author weicheng.zhao
 * @date 2020/12/26
 */
@Repository
@Mapper
public interface UserMapper {
	
	@Select("select user_name, `password` from tb_user where user_name = #{userName} and `password` = #{password}")
	User selectByUserNameAndPassword(@Param("userName") String userName, @Param("password") String password);
	
}
