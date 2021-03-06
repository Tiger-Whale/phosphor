package com.cafebabe.phosphor.service;

import com.cafebabe.phosphor.model.dto.TeacherInformationDTO;
import com.cafebabe.phosphor.model.entity.Teacher;
import com.cafebabe.phosphor.util.PageModel;

import java.util.List;

/**
 * @author kevingx2016@gmail.com
 * <p>
 * class_name: TeacherService
 * <p>
 * create_date: 2018/10/17
 * <p>
 * create_time: 09:47
 * <p>
 * description: 教师 Service
 **/
public interface TeacherService {

    /**
     * 获取所有在职的教师的信息
     *
     * @param startRecord 分页信息
     * @return 在职的教师信息
     */
    PageModel<TeacherInformationDTO> getTeacherList(Integer startRecord);

    /**
     * 根据教师ID获取某个老师的信息
     *
     * @param teacherId 教师ID
     * @return 教师信息
     */
    TeacherInformationDTO getTeacherById(Integer teacherId);

    /**
     * 根据公司ID获取属于该公司的所有在职教师
     *
     * @param companyId 公司ID
     * @return 某公司所有在职教师
     */
    List<Teacher> getTeacherByCompanyId(Integer companyId);
}

