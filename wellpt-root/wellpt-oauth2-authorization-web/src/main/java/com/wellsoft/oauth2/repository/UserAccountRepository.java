package com.wellsoft.oauth2.repository;

import com.wellsoft.oauth2.entity.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/21    chenq		2019/9/21		Create
 * </pre>
 */
public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {
    @Modifying
    @Query("update UserAccountEntity set password=:_new where accountNumber=:accoutNumber and password=:old")
    int updateUserPassword(@Param("accoutNumber") String accoutNumber, @Param("old") String old,
                           @Param("_new") String aNew);


    @Modifying
    @Query("update UserAccountEntity set password=:_new where accountNumber=:accoutNumber  ")
    int updateUserPassword(@Param("accoutNumber") String accoutNumber,
                           @Param("_new") String aNew);
}
