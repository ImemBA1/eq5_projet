package com.gestionnaire_de_stage.service;

import com.gestionnaire_de_stage.exception.CurriculumAlreadyTreatedException;
import com.gestionnaire_de_stage.exception.IdDoesNotExistException;
import com.gestionnaire_de_stage.model.Curriculum;
import com.gestionnaire_de_stage.model.Student;
import com.gestionnaire_de_stage.repository.CurriculumRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hibernate.validator.internal.util.Contracts.assertTrue;

@Service
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final StudentService studentService;

    public CurriculumService(CurriculumRepository curriculumRepository, StudentService studentService) {
        this.curriculumRepository = curriculumRepository;
        this.studentService = studentService;
    }


    public Optional<Curriculum> convertMultipartFileToCurriculum(MultipartFile file, Long studentId) throws IOException {
        try {
            Student student = studentService.getOneByID(studentId);
            if (student != null) {
                String fileName = StringUtils.cleanPath(file.getOriginalFilename());

                Curriculum curriculum = new Curriculum(
                        fileName,
                        file.getContentType(),
                        file.getBytes(),
                        student
                );
                return Optional.of(curriculum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Curriculum createCurriculum(Curriculum curriculum) {
        return curriculumRepository.save(curriculum);
    }

    public Curriculum getCurriculum(Long id) {
        return curriculumRepository.getById(id);
    }

    public Stream<Curriculum> getAllCurriculumByValidity(boolean validity) {
        return curriculumRepository.findAllByIsValid(validity).stream();
    }

    public List<Student> findAllStudentsWithCurriculumNotValidatedYet() {
        List<Curriculum> curriculumNotValidatedYet = curriculumRepository.findAllByIsValidIsNull();

        return curriculumNotValidatedYet.stream().map(Curriculum::getStudent).collect(Collectors.toList());
    }

    public boolean validate(Long idCurriculum, boolean valid) throws IdDoesNotExistException, CurriculumAlreadyTreatedException, IllegalArgumentException {
        assertTrue(idCurriculum != null, "Erreur: Le id du curriculum ne peut pas etre null");

        Optional<Curriculum> curriculumOptional = curriculumRepository.findById(idCurriculum);

        if (curriculumOptional.isEmpty())
            throw new IdDoesNotExistException();
        if (curriculumOptional.get().getIsValid() != null)
            throw new CurriculumAlreadyTreatedException();

        Curriculum curriculum = curriculumOptional.get();
        curriculum.setIsValid(valid);
        curriculumRepository.save(curriculum);
        return true;
    }

    public Curriculum findOneById(Long idCurriculum) throws IllegalArgumentException {
        return null;
    }
}
