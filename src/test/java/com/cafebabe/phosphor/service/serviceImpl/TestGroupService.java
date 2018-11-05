package com.cafebabe.phosphor.service.serviceImpl;


import com.cafebabe.phosphor.dao.GroupDAO;
import com.cafebabe.phosphor.model.dto.CourseInfo;
import com.cafebabe.phosphor.model.entity.Group;
import com.cafebabe.phosphor.service.serviceimpl.CourseServiceImpl;
import com.cafebabe.phosphor.service.serviceimpl.GroupServiceImpl;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TestGroupService {
    BeanFactory beanFactory = new ClassPathXmlApplicationContext("config/applicationContext.xml");

    GroupDAO groupDAO = (GroupDAO) beanFactory.getBean("groupDAO");
    CourseServiceImpl courseService =(CourseServiceImpl) beanFactory.getBean("courseServiceImpl");
    @Test
    public void testGetGroupListAll(){
        List<Group> list = groupDAO.getGroupListAll();
        list.forEach(System.out::println);
    }
    @Test
    public void testGetGroupListByDiscountScope(){
        List<Group> list = groupDAO.getGroupListByDiscountScope(new BigDecimal(0.9),new BigDecimal(1.0));
        list.forEach(System.out::println);
    }
    @Test
    public void testGetGroupById(){
        Group list = groupDAO.getGroupById(10001);

        System.out.println(list);
    }
    @Test
    public void testConflict(){
        List<CourseInfo> courseInfos = new ArrayList<>();
        courseInfos.add(courseService.getCourseInfoService(10000));
        CourseInfo courseInfo =courseService.getConflictCourseInfo(courseInfos,10016);
        System.out.println(courseInfo);
    }
}