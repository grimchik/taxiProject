package paymentservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import paymentservice.dto.PaymentWithIdDTO;
import paymentservice.entity.Payment;

@Mapper
public interface PaymentWithIdMapper {
    PaymentWithIdMapper INSTANCE = Mappers.getMapper(PaymentWithIdMapper.class);
    @Mapping(source = "id",target = "id")
    PaymentWithIdDTO toDTO (Payment payment);
    @Mapping(source = "id",target = "id")
    Payment toEntity(PaymentWithIdDTO paymentWithIdDTO);
}
