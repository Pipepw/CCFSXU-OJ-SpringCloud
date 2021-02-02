package org.vervandlung.voj.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.verwandlung.voj.web.WebApplication;
import org.verwandlung.voj.web.mapper.OptionMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WebApplication.class)
@Rollback
public class OptionMapperTest {
    @Autowired(required=false)
    private OptionMapper optionMapper;
    @Test
    public void test(){
        System.out.println(optionMapper.getOption("websiteName"));
    }
}
