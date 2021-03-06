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
import com.cafebabe.phosphor.util.RedisUtil;
import com.cafebabe.phosphor.util.priceimpl.PriceOneImpl;
import com.cafebabe.phosphor.util.priceimpl.PriceTwoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


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


    private static final Integer RECOMMEND_GROUP = 3;
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
    @Transactional(rollbackFor = Exception.class)
    public Integer insertGroupDTO(GroupDTO groupDTO) {
        groupDAO.insertGroup(toGroup(groupDTO));
        Integer groupId = groupDAO.getGroupId();
        for (CourseInfo cours : groupDTO.getCourseInfos()) {
            groupCourseDAO.insertGroupCourse(new GroupCourse(null,
                    cours.getCourseId(),groupId,"1"));
        }
        return groupId;
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

        List<GroupDTO> cache = RedisUtil.getList("getGroupListAll");
        if (cache != null&&cache.size()>0){
            return cache;

        }else {
            List<Group> groups = groupDAO.getGroupListAlive();
            List<GroupDTO> groupDTOS = new ArrayList<>();
            for (Group group : groups) {
                groupDTOS.add(toGroupDTO(group));
            }
            RedisUtil.setList("getGroupListAll",groupDTOS);
            return groupDTOS;
        }
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
    @Override
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

    @Override
    public List<GroupDTO> getGroupByTime() {
        List<GroupDTO> groupDTOS = this.getGroupListAlive();
        groupDTOS.sort(Comparator.comparing(GroupDTO::getGroupCreateTime));
        return groupDTOS;
    }

    @Override
    public List<GroupDTO> getGroupByPriceAsc() {
        List<GroupDTO> groupDTOS = this.getGroupListAlive();
        groupDTOS.sort(Comparator.comparing(GroupDTO::getGroupPrice));
        return groupDTOS;
    }

    @Override
    public List<GroupDTO> getGroupByPriceDesc() {
        List<GroupDTO> groupDTOS = this.getGroupListAlive();
        groupDTOS.sort(Comparator.comparing(GroupDTO::getGroupPrice));
        List<GroupDTO> groupDTOList = new ArrayList<>();
        for (int i = groupDTOS.size()-1; i >=0; i--) {
            groupDTOList.add(groupDTOS.get(i));
        }
        return groupDTOList;
    }

    @Override
    public GroupDTO addCourseToGroup(GroupDTO groupDTO,Integer courseId){
        CourseInfo courseInfo = courseDAO.getCourseInfo(courseId);
        if (!groupDTO.getCourseInfos().contains(courseInfo)){
            groupDTO.getCourseInfos().add(courseInfo);
        }
        groupDTO.setGroupPrice(getPrice(groupDTO.getCourseInfos()));
        groupDTO.setGroupCourseNumber(groupDTO.getGroupCourseNumber()+1);
        groupDTO.setGroupCourseNumber(groupDTO.getCourseInfos().size());
        return groupDTO;
    }

    @Override
    public GroupDTO delCourseFromCourse(GroupDTO groupDTO, Integer courseId) {
        for (int i = groupDTO.getCourseInfos().size()-1; i >=0 ; i--) {
            if (groupDTO.getCourseInfos().get(i).getCourseId().equals(courseId)
                    ||groupDTO.getCourseInfos().get(i)==null) {
                groupDTO.getCourseInfos().remove(i);
            }
        }
        if(groupDTO.getCourseInfos().size()==0){
            groupDTO.setGroupPrice(new BigDecimal(0));
        }else {
            groupDTO.setGroupPrice(getPrice(groupDTO.getCourseInfos()));
        }
        groupDTO.setGroupCourseNumber(groupDTO.getCourseInfos().size());
        return groupDTO;
    }

    @Override
    public List<GroupDTO> getGroupListRecommend() {
        List<GroupDTO> cache = RedisUtil.getList("getGroupListRecommend");
        if (cache != null&&cache.size()>0){
            return cache;
        }else {
            List<Group> groups = groupDAO.getGroupListAlive();
            groups.sort(Comparator.comparing(Group::getGroupCreateTime));
            List<GroupDTO> groupDTOList = new ArrayList<>();
            for (int i = 0; i <RECOMMEND_GROUP ; i++) {
                groupDTOList.add(toGroupDTO(groups.get(i)));
            }
            RedisUtil.setList("getGroupListRecommend",groupDTOList);
            return groupDTOList;
        }
    }

    /**
     * 获取价格
     * @param courseInfos 课程列表
     * @return 价格
     */
    private BigDecimal getPrice(List<CourseInfo> courseInfos){
        int twoCourse;
        twoCourse = 2;
        int threeCourse = 3;
        Price price;
        if(courseInfos.size() == 1){
            price= new PriceOneImpl();
        }else if (courseInfos.size() == twoCourse) {
            price= new PriceTwoImpl();
        }else if(courseInfos.size() == threeCourse){
            price = new PriceTwoImpl();
        }else{
            price = new PriceTwoImpl();
        }
        return price.getPriceCountInfo(courseInfos);
    }

    /**
     * 将DTO转为对象
     * @param groupDTO 套餐传输
     * @return 对象
     */
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

    /**
     * 将对象转为DTO
     * @param group 对象
     * @return 套餐传输
     */
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
