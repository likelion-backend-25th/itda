package net.likelion.bebc25.itda.theme.mapper;

<<<<<<< Updated upstream
=======
//import net.likelion.bebc25.itda.dto.PageResponse;
>>>>>>> Stashed changes
import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ThemeMapper {

    // 테마 전체 목록 조회
    List<ThemeResponse> findAllThemes(@Param("memberId") Long memberId, @Param("limit") int limit, @Param("offset") int offset);

    // ID 기반 테마 단건 조회
    ThemeResponse findById(@Param("id") Long id);

    long countThemes();

}
