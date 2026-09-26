package net.likelion.bebc25.itda.auth.mapper;

import com.nimbusds.oauth2.sdk.token.RefreshToken;
import net.likelion.bebc25.itda.auth.dto.RefreshTokenResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefreshTokenMapper {

    void save(RefreshTokenResponse refreshToken);

    RefreshTokenResponse findValidToken(@Param("memberId") Long memberId, @Param("hashToken") String hashToken);

    void revoke(Long id);
}