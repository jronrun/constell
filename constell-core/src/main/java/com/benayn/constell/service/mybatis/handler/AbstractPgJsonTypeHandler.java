package com.benayn.constell.service.mybatis.handler;

import com.alibaba.fastjson.JSON;
import com.google.common.reflect.TypeToken;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.postgresql.util.PGobject;

//@MappedTypes({Object.class, CreateBy.class})
@MappedJdbcTypes(JdbcType.OTHER)
public abstract class AbstractPgJsonTypeHandler<T> extends BaseTypeHandler {

    @SuppressWarnings("unchecked")
    private final Class<T> handlerType = (Class<T>) new TypeToken<T>(getClass()) {}.getRawType();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
        throws SQLException {
        PGobject pGobject = new PGobject();
        pGobject.setType("jsonb");
        pGobject.setValue(JSON.toJSONString(parameter));

        ps.setObject(i, pGobject);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return JSON.parseObject(rs.getString(columnName), handlerType);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return JSON.parseObject(rs.getString(columnIndex), handlerType);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return JSON.parseObject(cs.getString(columnIndex), handlerType);
    }

}
