package by.bsu.n1jel.pc.assembler.exception.common;

public class ResourceExceptionFactory {

    public static ResourceNotFoundException specificationTypeNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Specification type buildId = %s not found".formatted(id));
    }

    public static ResourceNotFoundException buildNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Build buildId = %s not found".formatted(id));
    }

    public static ResourceNotFoundException buildPartitionNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Build partition buildId = %s not found".formatted(id));
    }

    public static ResourceNotFoundException componentNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Component buildId = %s not found".formatted(id));
    }

    public static ResourceNotFoundException componentTypeNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Component type buildId = %s not found".formatted(id));
    }

    public static ResourceNotFoundException producerNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Producer buildId = %s not found".formatted(id));
    }

    public static ResourceNotFoundException specificationNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Specification buildId = %s not found".formatted(id));
    }

}
