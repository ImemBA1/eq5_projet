package com.gestionnaire_de_stage.service;

import com.gestionnaire_de_stage.dto.CurriculumDTO;
import com.gestionnaire_de_stage.exception.IdDoesNotExistException;
import com.gestionnaire_de_stage.model.*;
import com.gestionnaire_de_stage.repository.CurriculumRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurriculumServiceTest {

    @InjectMocks
    private CurriculumService curriculumService;

    @Mock
    private CurriculumRepository curriculumRepository;

    @Mock
    private StudentService studentService;

    @Test
    public void testConvertMultipartFileToCurriculum_WithValidData() throws IOException, IdDoesNotExistException {
        Student student = new Student();
        student.setId(1L);
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        when(studentService.getOneByID(any())).thenReturn(student);

        Curriculum actualCurriculum = curriculumService.convertMultipartFileToCurriculum(file, student.getId());

        assertThat(actualCurriculum.getStudent()).isEqualTo(student);
    }

    @Test
    public void testConvertMultipartFileToCurriculum_withNullFile() {
        Student student = new Student();

        assertThrows(IllegalArgumentException.class,
                () -> curriculumService.convertMultipartFileToCurriculum(null, student.getId()));
    }

    @Test
    public void testConvertMultipartFileToCurriculum_withNullStudentID() {
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());

        assertThrows(IllegalArgumentException.class,
                () -> curriculumService.convertMultipartFileToCurriculum(file, null));
    }

    @Test
    public void testConvertMultipartFileToCurriculum_doesntExistStudentID() throws Exception {
        Student student = new Student();
        student.setId(1L);
        MockMultipartFile file = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        when(studentService.getOneByID(any())).thenThrow(IdDoesNotExistException.class);

        assertThrows(IdDoesNotExistException.class,
                () -> curriculumService.convertMultipartFileToCurriculum(file, student.getId()));
    }

    @Test
    public void testCreate_withValidCurriculum() {
        Curriculum dummyCurriculum = getDummyCurriculum();
        when(curriculumRepository.save(any())).thenReturn(dummyCurriculum);

        Curriculum actualCurriculum = curriculumService.create(dummyCurriculum);

        assertThat(actualCurriculum).isEqualTo(dummyCurriculum);
    }

    @Test
    public void testCreate_withNullCurriculum() {
        assertThrows(IllegalArgumentException.class,
                () -> curriculumService.create(null));
    }

    @Test
    public void testGetByID_withValidID() throws Exception {
        Long validID = 1L;
        Curriculum dummyCurriculum = getDummyCurriculum();
        when(curriculumRepository.existsById(any())).thenReturn(true);
        when(curriculumRepository.getById(any())).thenReturn(dummyCurriculum);

        Curriculum actualCurriculum = curriculumService.getOneByID(validID);

        assertThat(actualCurriculum).isEqualTo(dummyCurriculum);
    }

    @Test
    public void testGetByID_withNullID() {
        assertThrows(IllegalArgumentException.class,
                () -> curriculumService.getOneByID(null));
    }

    @Test
    public void testGetByID_doesntExistID() {
        Long invalidID = 5L;
        when(curriculumRepository.existsById(any())).thenReturn(false);

        assertThrows(IdDoesNotExistException.class,
                () -> curriculumService.getOneByID(invalidID));
    }

    @Test
    public void testGetAll() {
        when(curriculumRepository.findAll()).thenReturn(getDummyCurriculumList());

        List<Curriculum> actualCurriculumList = curriculumService.getAll();

        assertThat(actualCurriculumList.size()).isGreaterThan(0);
    }

    @Test
    public void testMapToCurriculumDTOList_withValidEntries() {
        List<OfferApplication> offerApplicationList = getDummyOfferAppList();
        List<CurriculumDTO> curriculumDTOList = getDummyCurriculumDTOList();

        List<CurriculumDTO> actualCurriculumDTOList = curriculumService.mapToCurriculumDTOList(offerApplicationList);

        assertThat(actualCurriculumDTOList.size()).isEqualTo(curriculumDTOList.size());
        assertThat(actualCurriculumDTOList.get(1).getFirstName()).isEqualTo(curriculumDTOList.get(1).getFirstName());
    }

    @Test
    public void testMapToCurriculumDTOList_withEmptyList() {
        List<OfferApplication> offerApplicationList = new ArrayList<>();
        assertThrows(IllegalArgumentException.class,
                () -> curriculumService.mapToCurriculumDTOList(offerApplicationList));
    }

    private Curriculum getDummyCurriculum() {
        Student dummyStudent = new Student();
        dummyStudent.setId(1L);

        return new Curriculum(
                "fileName",
                "content type",
                "test".getBytes(),
                dummyStudent
        );
    }

    private List<Curriculum> getDummyCurriculumList() {
        Curriculum dummyCurriculum1 = getDummyCurriculum();
        Curriculum dummyCurriculum2 = getDummyCurriculum();
        Curriculum dummyCurriculum3 = getDummyCurriculum();

        return Arrays.asList(dummyCurriculum1, dummyCurriculum2, dummyCurriculum3);
    }

    private Curriculum getDummyCurriculumOffer() {
        Curriculum dummyCurriculum = new Curriculum();

        dummyCurriculum.setId(1L);
        dummyCurriculum.setData("some xml".getBytes());
        dummyCurriculum.setName("fileeeename");
        dummyCurriculum.setStudent(getDummyStudent());
        return dummyCurriculum;
    }

    private Student getDummyStudent() {
        Student dummyStudent = new Student();
        dummyStudent.setId(1L);
        dummyStudent.setLastName("Winter");
        dummyStudent.setFirstName("Summer");
        dummyStudent.setEmail("cant@outlook.com");
        dummyStudent.setPassword("cantPass");
        dummyStudent.setDepartment("info");
        dummyStudent.setMatricule("4673943");
        return dummyStudent;
    }

    private Offer getDummyOffer() {
        Offer dummyOffer = new Offer();
        dummyOffer.setDepartment("Un departement");
        dummyOffer.setAddress("ajsaodas");
        dummyOffer.setId(1L);
        dummyOffer.setDescription("oeinoiendw");
        dummyOffer.setSalary(10);
        dummyOffer.setTitle("oeinoiendw");
        dummyOffer.setCreator(getDummyMonitor());
        return dummyOffer;
    }

    private Monitor getDummyMonitor() {
        Monitor dummyMonitor = new Monitor();
        dummyMonitor.setId(1L);
        dummyMonitor.setLastName("toto");
        dummyMonitor.setFirstName("titi");
        dummyMonitor.setEmail("toto@gmail.com");
        dummyMonitor.setPassword("testPassword");
        return dummyMonitor;
    }

    private List<OfferApplication> getDummyOfferAppList() {
        List<OfferApplication> dummyOfferApplicationList = new ArrayList<>();
        OfferApplication dummyOfferApplication = new OfferApplication();
        dummyOfferApplication.setOffer(getDummyOffer());
        dummyOfferApplication.setCurriculum(getDummyCurriculumOffer());
        dummyOfferApplication.setId(1L);
        dummyOfferApplicationList.add(dummyOfferApplication);

        dummyOfferApplication.setId(2L);
        dummyOfferApplicationList.add(dummyOfferApplication);

        dummyOfferApplication.setId(3L);
        dummyOfferApplicationList.add(dummyOfferApplication);

        return dummyOfferApplicationList;
    }

    private List<CurriculumDTO> getDummyCurriculumDTOList() {
        List<CurriculumDTO> curriculumDTOList = new ArrayList<>();
        CurriculumDTO curriculumDTO1 = new CurriculumDTO();
        curriculumDTO1.setFirstName("Summer");
        curriculumDTO1.setLastName("Winter");
        curriculumDTO1.setFileName("SW_CV");
        curriculumDTO1.setFile(new byte[65 * 1024]);
        curriculumDTOList.add(curriculumDTO1);

        CurriculumDTO curriculumDTO2 = new CurriculumDTO();
        curriculumDTO2.setFirstName("Summer");
        curriculumDTO2.setLastName("Winter");
        curriculumDTO2.setFileName("SW_CV");
        curriculumDTO2.setFile(new byte[65 * 1024]);
        curriculumDTOList.add(curriculumDTO2);

        CurriculumDTO curriculumDTO3 = new CurriculumDTO();
        curriculumDTO3.setFirstName("Summer");
        curriculumDTO3.setLastName("Winter");
        curriculumDTO3.setFileName("SW_CV");
        curriculumDTO3.setFile(new byte[65 * 1024]);
        curriculumDTOList.add(curriculumDTO3);

        return curriculumDTOList;
    }
}
