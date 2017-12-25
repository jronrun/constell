package com.benayn.constell.services.capricorn.repository;

import com.alibaba.fastjson.JSON;
import com.benayn.constell.services.capricorn.repository.domain.Content;
import com.benayn.constell.services.capricorn.repository.domain.ContentExample;
import com.benayn.constell.services.capricorn.repository.mapper.ContentMapper;
import com.benayn.constell.services.capricorn.type.Language;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContentRepositoryTest {

    @Autowired
    ContentMapper contentMapper;

    @Test
    public void testAA() {
        Content content = new Content();
        content.setContent("test");

        content.setCreateTime(new Date());

        content.setLanguage(Language.of("java", "x/javac"));

        content.setLastModifyTime(new Date());
        content.setNote("test");
        content.setStatus((short)1);
        content.setSummary("summary");
        content.setTags(Lists.newArrayList(1L,22L,33L,55L));
        content.setTitle("title");
        content.setUserId(1111L);

        int result = contentMapper.insertSelective(content);
        System.out.println(result);
        Content cc = contentMapper.selectByPrimaryKey(content.getId());
        System.out.println(cc);
        //noinspection unchecked
        List<Long> tags = cc.getTags();
        System.out.println(tags);

        ContentExample example = new ContentExample();
        example.createCriteria()
            ;

        System.out.println(JSON.toJSONString(contentMapper.selectByExample(example)));
    }

}
