package paymentservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import paymentservice.dto.PaymentDTO;
import paymentservice.entity.Payment;

@Mapper
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);
    PaymentDTO toDTO (Payment payment);
    Payment toEntity(PaymentDTO paymentDTO);
}
