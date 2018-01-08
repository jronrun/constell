package com.benayn.constell.services.capricorn.viewobject;

import static com.benayn.constell.service.server.repository.domain.ConditionTemplate.LIKE;
import static com.benayn.constell.service.server.respond.HtmlTag.SELECT;
import static com.benayn.constell.services.capricorn.config.Authorities.TAG_CREATE;
import static com.benayn.constell.services.capricorn.config.Authorities.TAG_DELETE;
import static com.benayn.constell.services.capricorn.config.Authorities.TAG_RETRIEVE;
import static com.benayn.constell.services.capricorn.config.Authorities.TAG_UPDATE;

import com.benayn.constell.service.server.respond.Accessable;
import com.benayn.constell.service.server.respond.Actionable;
import com.benayn.constell.service.server.respond.DefineElement;
import com.benayn.constell.service.server.respond.Editable;
import com.benayn.constell.service.server.respond.HtmlTag;
import com.benayn.constell.service.server.respond.Listable;
import com.benayn.constell.service.server.respond.Renderable;
import com.benayn.constell.service.server.respond.Searchable;
import com.benayn.constell.services.capricorn.enums.ColorCode;
import com.benayn.constell.services.capricorn.enums.TagType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Actionable
@Accessable(
    create = TAG_CREATE,
    retrieve = TAG_RETRIEVE,
    update = TAG_UPDATE,
    delete = TAG_DELETE)
public class TagVO extends Renderable {

    @DefineElement("render.common.id")
    @Searchable
    @Listable
    @Editable(hidden = true)
    private Long id;

    @NotNull
    @Pattern(regexp = "^TAG_.*", message = "{valid.tag.code.pattern}")
    @DefineElement("render.tag.code")
    @Searchable(condition = LIKE)
    @Listable
    @Editable
    private String code;

    @NotNull
    @Size(min = 1)
    @DefineElement("render.tag.label")
    @Searchable(condition = LIKE)
    @Listable
    @Editable(tag = HtmlTag.TEXTAREA, attributes = {"rows=3"})
    private String label;

    @DefineElement(value = "render.tag.type", options = TagType.class, tag = SELECT)
    @Editable
    @Listable
    private Short type;

    @DefineElement(value = "render.tag.color", options = ColorCode.class, tag = SELECT)
    @Editable
    @Listable
    private String color;

    @DefineElement("render.common.createTime")
    @Listable
    private String createTime;

}
