package com.benayn.constell.service.server.respond;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class QueryCondition {

    public static final int DEFAULT_PAGE_SIZE = 1;

    private int pageNo;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private List<String> likeFields;

    public void addLikeField(String fieldName) {
        if (null == likeFields) {
            likeFields = Lists.newArrayList();
        }

        likeFields.add(fieldName);
    }

    public boolean isLike(String fieldName) {
        if (null == likeFields) {
            return false;
        }

        return likeFields.contains(fieldName);
    }
}
