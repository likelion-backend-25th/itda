package net.likelion.bebc25.itda.admin.theme.mapper;

import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeResponse;
import net.likelion.bebc25.itda.admin.theme.dto.AdminThemeRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Mapper
public interface AdminThemeMapper {

    // 전체 테마 조회
    List<AdminThemeResponse> findAllThemes();

    // 테마 등록
    int insertTheme(AdminThemeRequest request);

    // 테마 정보 수정
    int updateTheme(
            @Param("themeId") Long themeId,
            @Param("request") AdminThemeRequest request
    );

    // 테마 판매 상태 변경
    int updateThemeStatus(
            @Param("themeId") Long themeId,
            @Param("status") String status
    );

    // 테마 코드 중복 방지
    boolean existsByThemeCode(@Param("themeCode") String themeCode);
}