package com.cafebabe.phosphor.service.serviceimpl;

import com.cafebabe.phosphor.dao.CourseDAO;
import com.cafebabe.phosphor.dao.GroupCourseDAO;
import com.cafebabe.phosphor.dao.GroupDAO;
import com.cafebabe.phosphor.model.dto.CourseInfo;
import com.cafebabe.phosphor.model.dto.GroupDTO;
import com.cafebabe.phosphor.model.entity.Group;
import com.cafebabe.phosphor.model.entity.GroupCourse;
import com.cafebabe.phosphor.service.GroupService;
import com.cafebabe.phosphor.util.Price;
import com.cafebabe.phosphor.util.priceimpl.PriceThreeImpl;
import com.cafebabe.phosphor.util.priceimpl.PriceTwoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author thethingyk@gmail.com
 *
 * class_name: GroupServiceImpl
 *
 * create_date: 2018/10/9
 *
 * create_time: 19:29
 *
 * description: 套餐service层的实现类
 **/

@Service
public class GroupServiceImpl implements GroupService {



    private final GroupDAO groupDAO;
    private final GroupCourseDAO groupCourseDAO;
    private final CourseDAO courseDAO;
    @Autowired
    public GroupServiceImpl(GroupDAO groupDAO, GroupCourseDAO groupCourseDAO, CourseDAO courseDAO) {
        this.groupDAO = groupDAO;
        this.groupCourseDAO = groupCourseDAO;
        this.courseDAO = courseDAO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer insertGroup(Group group, List<Integer> courses) {
        for (Integer cours : courses) {
            groupCourseDAO.insertGroupCourse(new GroupCourse(null,
                    group.getGroupId(),cours,"1"));
        }
        return groupDAO.insertGroup(group);
    }

    @Override
    public Integer updateGroup(Group group) { return groupDAO.updateGroup(group); }

    @Override
    public Integer updateGroupDiscount(BigDecimal groupDiscount, Integer groupId) {
        return groupDAO.updateGroupDiscount(groupDiscount,groupId);
    }

    @Override
    public Integer updateGroupStatus(Integer groupStatus, Integer groupId) {
        return groupDAO.updateGroupStatus(groupStatus,groupId);
    }

    @Override
    public Integer removeGroup(Integer id) { return groupDAO.removeGroup(id); }

    @Override
    public Group getGroupById(Integer id) { return groupDAO.getGroupById(id); }

    @Override
    public GroupDTO getGroupDTOById(Integer id) {
        return toGroupDTO(groupDAO.getGroupById(id));
    }

    @Override
    public List<Group> getGroupListAll() {
        return groupDAO.getGroupListAll();
    }

    @Override
    public List<CourseInfo> getCourseListByGroupId(Integer groupId) {
        List<GroupCourse> groupCourses = groupCourseDAO.getGroupCourse(groupId);
        List<CourseInfo> courseInfos = new ArrayList<>();
        for (GroupCourse groupCours : groupCourses) {
            courseInfos.add(courseDAO.getCourseInfo(groupCours.getCourseId()));
        }
        return courseInfos;
    }

    @Override
    public List<GroupDTO> getGroupListAlive() {

        List<Group> groups = groupDAO.getGroupListAlive();
        List<GroupDTO> groupDTOS = new ArrayList<>();
        for (Group group : groups) {
            groupDTOS.add(toGroupDTO(group));
        }
        return groupDTOS;
    }

    @Override
    public List<GroupDTO> getGroupListByDiscount(BigDecimal discount) {
        List<Group> groups = groupDAO.getGroupListByDiscount(discount);
        List<GroupDTO> groupDTOS = new ArrayList<>();
        for (Group group : groups) {
            groupDTOS.add(toGroupDTO(group));
        }
        return groupDTOS;

    }

    @Override
    public List<GroupDTO> getGroupListByDiscountScope(BigDecimal minDiscount, BigDecimal maxDiscount) {

        List<Group> groups = groupDAO.getGroupListByDiscountScope(minDiscount,maxDiscount);
        List<GroupDTO> groupDTOS = new ArrayList<>();
        for (Group group : groups) {
            groupDTOS.add(toGroupDTO(group));
        }
        return groupDTOS;
    }

    public GroupDTO createGroup(Integer courseId){
        GroupDTO groupDTO = new GroupDTO();
        CourseInfo courseInfo = courseDAO.getCourseInfo(courseId);
        List<CourseInfo> courseInfos = new ArrayList<>();
        courseInfos.add(courseInfo);
        groupDTO.setCourseInfos(courseInfos);
        groupDTO.setGroupName("自定义套餐");
        groupDTO.setGroupPrice(courseInfo.getCoursePrice());
        groupDTO.setGroupDiscount(new BigDecimal(1.00));
        groupDTO.setGroupCourseNumber(1);
        groupDTO.setGroupStatus(0);
        return groupDTO;

    }

    public GroupDTO addCourseToGroup(GroupDTO groupDTO,Integer courseId){
        CourseInfo courseInfo = courseDAO.getCourseInfo(courseId);
        Price price;
        if (groupDTO.getCourseInfos().size() == 2) {
            price= new PriceTwoImpl();
        }else if(groupDTO.getCourseInfos().size() == 3){
            price = new PriceTwoImpl();
        }else{
            price = new PriceTwoImpl();
        }
        groupDTO.setGroupPrice(price.getPriceCountInfo(groupDTO.getCourseInfos()));
        groupDTO.setGroupCourseNumber(groupDTO.getGroupCourseNumber()+1);
        return groupDTO;
    }

    private Group toGroup(GroupDTO groupDTO){
        if (groupDTO == null) {
            return null;
        }
        return new Group(groupDTO.getGroupId(),
                groupDTO.getGroupStatus(),
                groupDTO.getGroupEndTime(),
                groupDTO.getGroupName(),
                groupDTO.getGroupDiscount(),
                groupDTO.getGroupContent(),
                groupDTO.getGroupCreateTime(),
                groupDTO.getGroupSf(),
                groupDTO.getGroupPrice(),
                groupDTO.getGroupCourseNumber(),
                groupDTO.getGroupPhoto()
        );
    }

    private GroupDTO toGroupDTO(Group group){
        if (group == null) {
            return null;
        }
        return new GroupDTO(group.getGroupId(),
                group.getGroupStatus(),
                group.getGroupEndTime(),
                group.getGroupName(),
                group.getGroupDiscount(),
                group.getGroupContent(),
                group.getGroupCreateTime(),
                group.getGroupSf(),
                group.getGroupPrice(),
                group.getGroupCourseNumber(),
                group.getGroupPhoto(),
                getCourseListByGroupId(group.getGroupId()));
    }

}