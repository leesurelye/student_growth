package com.info.converter;

import com.info.dto.ArticleDTO;
import com.info.entity.ArticleEntity;
import com.info.mapper.provider.ArticleProvider;
import org.mapstruct.Mapper;

/**
 * @author : yue
 * @Date : 2020/7/19 / 20:19
 */
@Mapper(componentModel = "spring")
public interface ArticleConverter {

    ArticleEntity articleConve(ArticleDTO dto);


    ArticleDTO articleEntityConve(ArticleEntity entity);

}