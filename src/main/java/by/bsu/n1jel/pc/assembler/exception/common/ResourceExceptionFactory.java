package by.bsu.n1jel.pc.assembler.exception.common;

public class ResourceExceptionFactory {

    public static ResourceNotFoundException specificationTypeNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Specification objectType id = %s not found".formatted(id));
    }

    public static ResourceNotFoundException buildNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Build id = %s not found".formatted(id));
    }

    public static ResourceNotFoundException buildPartitionNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Build partition id = %s not found".formatted(id));
    }

    public static ResourceNotFoundException componentNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Component id = %s not found".formatted(id));
    }

    public static ResourceNotFoundException componentTypeNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Component objectType id = %s not found".formatted(id));
    }

    public static ResourceNotFoundException producerNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Producer id = %s not found".formatted(id));
    }

    public static ResourceNotFoundException specificationNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Specification id = %s not found".formatted(id));
    }
    public static ResourceNotFoundException imageNotFoundException(Long id){
        return new ResourceNotFoundException(id, "Image id = %s not found".formatted(id));
    }

}
