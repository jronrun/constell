package com.benayn.constell.services.capricorn.repository.mapper;

import com.benayn.constell.services.capricorn.repository.domain.ShareAccess;
import com.benayn.constell.services.capricorn.repository.domain.ShareAccessExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ShareAccessMapper {
    long countByExample(ShareAccessExample example);

    int deleteByExample(ShareAccessExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ShareAccess record);

    int insertSelective(ShareAccess record);

    List<ShareAccess> selectByExample(ShareAccessExample example);

    ShareAccess selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ShareAccess record, @Param("example") ShareAccessExample example);

    int updateByExample(@Param("record") ShareAccess record, @Param("example") ShareAccessExample example);

    int updateByPrimaryKeySelective(ShareAccess record);

    int updateByPrimaryKey(ShareAccess record);
}