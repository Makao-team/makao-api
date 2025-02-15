package kr.co.makao.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.co.makao.annotation.ValidFiles;
import org.springframework.web.multipart.MultipartFile;

public class FilesValidator implements ConstraintValidator<ValidFiles, MultipartFile[]> {
    private long maxSize;

    @Override
    public void initialize(ValidFiles constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile[] files, ConstraintValidatorContext context) {
        if (files == null) return true;

        for (MultipartFile file : files) {
            if (file != null && file.getSize() > maxSize) return false;
        }
        return true;
    }
}
