package com.benayn.constell.service.mybatis.handler;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.TypeException;

//@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.ARRAY)
public class AbstractPgArrayTypeHandler<T> extends BaseTypeHandler {

    @SuppressWarnings("unchecked")
    private final Class<T> handlerType = (Class<T>) new TypeToken<T>(getClass()) {}.getRawType();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
        throws SQLException {
        @SuppressWarnings("unchecked") List<T> theParameter = (List<T>) parameter;
        String typeName = getTypeName();
        Connection conn = ps.getConnection();
        Array array = conn.createArrayOf(typeName, theParameter.toArray());
        ps.setArray(i, array);
    }

    private String getTypeName() {
        String typeName = null;
        Class<?> componentType = handlerType;
        if (componentType.isAssignableFrom(Short.class)) {
            typeName = "smallint";
        } else if (componentType.isAssignableFrom(Integer.class)) {
            typeName = "int";
        } else if (componentType.isAssignableFrom(Long.class)) {
            typeName = "bigint";
        } else if (componentType.isAssignableFrom(Double.class)
            || componentType.isAssignableFrom(Float.class)) {
            typeName = "float";
        } else if (componentType.isAssignableFrom(Boolean.class)) {
            typeName = "boolean";
        } else if (componentType.isAssignableFrom(BigDecimal.class)) {
            typeName = "decimal";
        }

        if (typeName == null) {
            throw new TypeException(
                "ArrayTypeHandler parameter typeName error, your type is " + componentType.getName());
        }

        return typeName;
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName)
        throws SQLException {
        return getArray(rs.getArray(columnName));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getArray(rs.getArray(columnIndex));
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex)
        throws SQLException {
        return getArray(cs.getArray(columnIndex));
    }

    @SuppressWarnings("unchecked")
    private List<T> getArray(Array array) throws SQLException {
        if (array == null) {
            return null;
        }

        Object obj = array.getArray();
        if (obj.getClass().isArray()) {
            return Lists.newArrayList((T[]) obj);
        }

        return null;
    }

}
