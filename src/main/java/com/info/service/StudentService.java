package com.info.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.info.common.ReturnValue;
import com.info.common.sysenum.StateMsg;
import com.info.entity.AssessEntity;
import com.info.entity.converter.Converter;
import com.info.dto.FileDTO;
import com.info.dto.ScoreDTO;
import com.info.dto.StudentInfoDto;
import com.info.entity.ImageEntity;
import com.info.entity.ScoreEntity;
import com.info.entity.Student;
import com.info.exception.SystemException;
import com.info.formbean.PageBean;
import com.info.mapper.AssessMapper;
import com.info.mapper.ScoreInfoMapper;
import com.info.mapper.StudentInfoMapper;
import com.info.util.EncryptUtil;
import com.info.util.FastClientUtil;
import io.swagger.annotations.ApiOperation;
import jdk.jfr.events.ThrowablesEvent;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : yue
 * @Date : 2020/7/8 / 20:21
 */
@Service
public class StudentService {
    @Resource
    private StudentInfoMapper studentMapper;

    @Resource
    private ScoreInfoMapper scoreMapper;

    @Resource
    private AssessMapper assessMapper;

    @Autowired
    private FastClientUtil clientUtil;


    private Converter<ScoreEntity> scoreConverter = new Converter<>();

    private Converter<Student> studentConverter = new Converter<>();


    @ApiOperation(value = "通过学号密码获取学生基本信息")
    public StudentInfoDto getStudentInfo(String id, String password) throws Exception {
        Student entity = studentMapper.getStudentById(id);
        if (null == entity) {
            throw new SystemException(StateMsg.StateMsg_202);
        }
        String encryptCode = EncryptUtil.encryptMD5(password);
        if (!entity.getPassword().equals(encryptCode)) {
            throw new SystemException(StateMsg.StateMsg_203);
        }

        return studentConverter.clone(entity, StudentInfoDto.class);

    }


    @ApiOperation("修改密码")
    public String updatePassword(String id, String password, String newPassword)
            throws SystemException {
        String pwd = studentMapper.getPassword(id);
        if (pwd == null || !pwd.equals(EncryptUtil.encryptMD5(password))) {
            throw new SystemException(StateMsg.StateMsg_202);
        }
        String updatePwd = EncryptUtil.encryptMD5(newPassword);
        int res = studentMapper.updatePassword(id, updatePwd);
        if (res <= 0) {
            throw new SystemException(StateMsg.StateMsg_500);
        }
        return StateMsg.StateMsg_200.getMsg();
    }

    @ApiOperation(value = "分页查询学生信息")
    public ReturnValue<Student> getStudentList(PageBean page)
            throws SystemException {
        if (page.getCurrentPage() <= 0) {
            throw new SystemException(StateMsg.StateMsg_200);
        }
        ReturnValue<Student> result = new ReturnValue<>();
        int start = (page.getCurrentPage() - 1) * page.getPageSize();
        List<Student> students = studentMapper.pageSelectStudent(start, page.getPageSize());
        result.setList(students);
        return result;
    }

    @ApiOperation("修改密码")
    public String changePassword(String id, String oldPassword)
            throws SystemException {
        String password = studentMapper.getPassword(id);
        if (!EncryptUtil.encryptMD5(password).equals(oldPassword)) {
            throw new SystemException(StateMsg.StateMsg_203);
        }
        return null;
    }


    @ApiOperation("获取成绩")
    public List<ScoreDTO> queryScoreById(String id) throws Exception {
        List<ScoreEntity> scores = scoreMapper.selectScoreById(id);
        if (null == scores || scores.size() == 0) {
            throw new SystemException(StateMsg.StateMsg_104);
        }
        return scores.stream().map(item ->
                scoreConverter.clone(item, ScoreDTO.class))
                .collect(Collectors.toList());
    }


    public Map<String, List<ScoreDTO>> groupQueryScore(String id) throws SystemException {
        List<ScoreEntity> scores = scoreMapper.selectScoreById(id);
        if (null == scores || scores.size() == 0) {
            throw new SystemException(StateMsg.StateMsg_104);
        }

        return scores.stream().map(item ->
                scoreConverter.clone(item, ScoreDTO.class))
                .collect(Collectors.groupingBy(ScoreDTO::getCourseType));
    }

    /**
     * 上传学生申请的照片
     *
     * @param file image
     * @param id   student_id
     * @return void
     * @throws Exception void
     */
    public FileDTO uploadFile(MultipartFile file, String id) throws Exception {
        FileDTO res = new FileDTO();
        StorePath storePath = clientUtil.upload(file);
        ImageEntity image = new ImageEntity();
        image.setName(file.getName());
        image.setPath(storePath.getPath());
        image.setImgGroup(storePath.getGroup());

        return res;
    }

    /**
     * 学生评估分数
     *
     * @param id    学号
     * @param psy   心理成绩
     * @param moral 品德成绩
     * @return void
     */
    public String assess(String id, String other, int psy, int moral) throws Exception {
        if (psy > 100 || moral > 100 || psy < 0 || moral < 0) {
            throw new SystemException(StateMsg.StateMsg_101);
        }
        //如果数据已经存在，则更新数据
        AssessEntity entity = new AssessEntity(id, other, psy, moral);
        if(assessMapper.selectByPrimary(id, other)!=null){
            int res = assessMapper.update(entity);
            return res==1?StateMsg.StateMsg_200.getMsg():StateMsg.StateMsg_500.getMsg();
        }
        Integer res = assessMapper.insert(entity);
        return res==1?StateMsg.StateMsg_200.getMsg():StateMsg.StateMsg_500.getMsg();
    }


    public byte[] downloadFile(String group, String path)
            throws Exception {
        return clientUtil.download(group, path);
    }
}
