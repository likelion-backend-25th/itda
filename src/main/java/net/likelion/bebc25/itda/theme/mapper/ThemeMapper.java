//package net.likelion.bebc25.itda.theme.mapper;
//
//import net.likelion.bebc25.itda.dto.PageResponse;
//import net.likelion.bebc25.itda.theme.dto.ThemeResponse;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//
//import java.util.List;
//import java.util.Map;
//
//@Mapper
//public interface ThemeMapper {
//
//    // 테마 전체 목록 조회
//    List<ThemeResponse> findAllThemes(@Param("memberId") Long memberId,
//                                              @Param("limit") int limit,
//                                              @Param("offset") int offset);
//
//    long countThemes();
//
//}
