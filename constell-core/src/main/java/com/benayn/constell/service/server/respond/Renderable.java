package com.benayn.constell.service.server.respond;

import static com.benayn.constell.service.common.BaseConstants.DEFAULT_PAGE_SIZE;

import com.benayn.constell.service.util.Likes;
import com.benayn.constell.service.util.Likes.Side;
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

    public String like(String target) {
        return Likes.get(target);
    }

    public String like(String target, Side side) {
        return Likes.get(target, side);
    }

    public boolean isLike(String fieldName) {
        return checkFields().contains(fieldName);
    }

    private List<String> checkFields() {
        if (null != likeFields) {
            return likeFields;
        }

        likeFields = Lists.newArrayList();
        Lists.newArrayList(getClass().getDeclaredFields()).forEach(field -> {
            Searchable searchable = field.getAnnotation(Searchable.class);
            if (null != searchable && searchable.like()) {
                likeFields.add(field.getName());
            }
        });

        return likeFields;
    }


}
