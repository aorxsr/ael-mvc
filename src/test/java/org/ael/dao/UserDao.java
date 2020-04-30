package org.ael.dao;

import org.ael.orm.annotation.Dao;
import org.ael.orm.annotation.dml.Query;

@Dao
public interface UserDao {

    @Query(sql = "asd")
    void q();

}
