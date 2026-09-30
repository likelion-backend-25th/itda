package net.likelion.bebc25.itda.commoncode.mapper;

import net.likelion.bebc25.itda.domain.CommonCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommonCodeMapper {

    CommonCode findActiveSubscriptionPrice(
            @Param("priceId") Long priceId
    );

    /** 게시글/관심사 카테고리 (type = 3) */
    CommonCode findActivePostCategory(
            @Param("categoryId") Long categoryId
    );
}
