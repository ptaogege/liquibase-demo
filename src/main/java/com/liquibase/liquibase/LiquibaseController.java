package com.liquibase.liquibase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

@Controller
public class LiquibaseController {
    private Logger logger = LoggerFactory.getLogger(LiquibaseController.class);

    @Resource
    private LiquibaseService liquibaseService;

    @RequestMapping("/diffChangeLog")
    @ResponseBody
    public String diffChangeLog(@RequestParam("diffChange") String diffChange,
                                @RequestParam("referenceUrl") String referenceUrl,
                                @RequestParam("referenceUsername") String referenceUsername,
                                @RequestParam("referencePassword") String referencePassword,
                                @RequestParam("targetUrl") String targetUrl,
                                @RequestParam("targetUsername") String targetUsername,
                                @RequestParam("targetPassword") String targetPassword) throws SQLException {
        liquibaseService.diffChangeLog(diffChange,referenceUrl,referenceUsername,referencePassword,targetUrl,targetUsername,targetPassword);
        return "success";
    }

    @RequestMapping("/update")
    @ResponseBody
    public String update(@RequestParam("tag") String tag) throws SQLException {

        liquibaseService.update(tag);
        return "success";
    }

    @RequestMapping("/GenerateChangeLog")
    @ResponseBody
    public String GenerateChangeLog(@RequestParam("outputChange") String outputChange) throws SQLException {
        liquibaseService.GenerateChangeLog(outputChange);
        return "success";
    }

    @RequestMapping("/changeLogSync")
    @ResponseBody
    public String changeLogSync(@RequestParam("changeLogSync") String changeLogSync) throws SQLException {
        liquibaseService.changeLogSync(changeLogSync);
        return "success";
    }

    @RequestMapping("/rollback")
    @ResponseBody
    public String rollback() throws SQLException {
        liquibaseService.rollback();
        return "success";
    }

}
