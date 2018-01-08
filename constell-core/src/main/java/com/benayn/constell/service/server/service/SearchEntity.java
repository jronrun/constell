package com.benayn.constell.service.server.service;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class SearchEntity {

    private String primaryKey;
    private List<SearchField> fields;

}
