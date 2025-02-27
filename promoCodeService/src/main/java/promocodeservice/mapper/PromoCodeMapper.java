package promocodeservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import promocodeservice.dto.PromoCodeDTO;
import promocodeservice.entity.PromoCode;

@Mapper
public interface PromoCodeMapper {
    PromoCodeMapper INSTANCE = Mappers.getMapper(PromoCodeMapper.class);
    @Mapping(source = "keyword",target = "keyword")
    PromoCodeDTO toDTO (PromoCode promoCode);
    @Mapping(source = "keyword",target = "keyword")
    PromoCode toEntity (PromoCodeDTO promoCodeDTO);
}
