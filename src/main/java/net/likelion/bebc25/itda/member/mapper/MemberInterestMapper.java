package net.likelion.bebc25.itda.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberInterestMapper {

    int insert(
            @Param("memberId") Long memberId,
            @Param("categoryId") Long categoryId
    );

    List<Long> findCategoryIdsByMemberId(@Param("memberId") Long memberId);

    int deleteByMemberId(@Param("memberId") Long memberId);
}
