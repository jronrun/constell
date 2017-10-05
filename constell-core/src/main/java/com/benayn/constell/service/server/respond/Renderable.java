package com.benayn.constell.service.server.respond;

import static com.benayn.constell.service.common.BaseConstants.DEFAULT_PAGE_SIZE;

import com.google.common.collect.Lists;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class Renderable {

    private boolean fragmentValue;

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
