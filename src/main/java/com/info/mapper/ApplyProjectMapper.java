package com.info.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.info.entity.ApplyProjectEntity;
import com.info.mapper.provider.ApplyProjectProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * @author : yue
 * @Date : 2020/8/22 / 22:19
 */
public interface ApplyProjectMapper extends BaseMapper<ApplyProjectEntity>{

    @SelectProvider(type = ApplyProjectProvider.class,
    method = "selectAll")
    List<ApplyProjectEntity> selectAll(@Param("sort") String sort);


    @SelectProvider(type = ApplyProjectProvider.class,
    method = "selectFormTempInMenu")
    String selectFormTemp(@Param("menuId") String menuId);


    @SelectProvider(type = ApplyProjectProvider.class,
            method = "selectFormTempInProject")
    String selectTempInProject(@Param("projectId") String projectId);


    @SelectProvider(type = ApplyProjectProvider.class,method = "fuzzySelect")
    List<ApplyProjectEntity> fuzzySelect(@Param("keyword") String keyword);
}
