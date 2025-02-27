package promocodeservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import promocodeservice.dto.PromoCodeWithIdDTO;
import promocodeservice.entity.PromoCode;
@Mapper
public interface PromoCodeWithIdMapper
{
    PromoCodeWithIdMapper  INSTANCE = Mappers.getMapper(PromoCodeWithIdMapper.class);
    @Mapping(source = "id",target = "id")
    PromoCodeWithIdDTO toDTO (PromoCode promoCode);
    @Mapping(source = "id",target = "id")
    PromoCode toEntity (PromoCodeWithIdDTO promoCodeWithIdDTO);
}
