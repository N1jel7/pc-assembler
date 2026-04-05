package by.bsu.n1jel.pc.assembler.mapper;

import by.bsu.n1jel.pc.assembler.dto.request.SpecificationInfoResponseDto;
import by.bsu.n1jel.pc.assembler.entity.Specification;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpecificationMapper {

    SpecificationInfoResponseDto mapToResponse(Specification specification);

    List<Specification> mapToResponse(List<Specification> specifications);
}
