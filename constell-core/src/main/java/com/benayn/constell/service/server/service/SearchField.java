package com.benayn.constell.service.server.service;

import com.benayn.constell.service.server.repository.domain.ConditionTemplate;
import com.benayn.constell.service.util.Likes.Side;
import lombok.Data;

@Data
public class SearchField {

    private ConditionTemplate conditionTemplate;
    private Side side;
    private String name;
    private Object value;

}
