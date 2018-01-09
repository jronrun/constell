package com.benayn.constell.services.capricorn.viewobject;

import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.Renderable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LanguageVO extends Renderable {

    @DefineElement("render.content.language.name")
    @Editable
    private String name;
    @DefineElement("render.content.language.mime")
    @Editable
    private String mime;

}
