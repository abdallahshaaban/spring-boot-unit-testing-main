package com.luv2code.springmvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

  private static MockHttpServletRequest request;

  @Autowired
  private JdbcTemplate jdbc;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private StudentDao studentDao;

  @Autowired
  private StudentAndGradeService studentService;

  @Mock
  private StudentAndGradeService studentCreateServiceMock;

  @Autowired
  private MathGradeDao mathGradeDao;

  @Value("${sql.script.create.student}")
  private String sqlAddStudent;

  @Value("${sql.script.create.math.grade}")
  private String sqlAddMathGrade;

  @Value("${sql.script.create.science.grade}")
  private String sqlAddScienceGrade;

  @Value("${sql.script.create.history.grade}")
  private String sqlAddHistoryGrade;

  @Value("${sql.script.delete.student}")
  private String sqlDeleteStudent;

  @Value("${sql.script.delete.math.grade}")
  private String sqlDeleteMathGrade;

  @Value("${sql.script.delete.science.grade}")
  private String sqlDeleteScienceGrade;

  @Value("${sql.script.delete.history.grade}")
  private String sqlDeleteHistoryGrade;


  @BeforeAll
  public static void setup() {
    request = new MockHttpServletRequest();
    request.setParameter("firstname", "Chad");
    request.setParameter("lastname", "Darby");
    request.setParameter("emailAddress", "chad_darby@luv2code_school.com");

  }

  @BeforeEach
  public void beforeEach() {
    jdbc.execute(sqlAddStudent);

    jdbc.execute(sqlAddMathGrade);
    jdbc.execute(sqlAddScienceGrade);
    jdbc.execute(sqlAddHistoryGrade);
  }

  @Test
  public void getStudentsHttpRequest() throws Exception {

    CollegeStudent studentOne = new GradebookCollegeStudent("Eric", "Roby",
        "eric_roby@luv2code_school.com");
    CollegeStudent studentTwo = new GradebookCollegeStudent("Chad", "Darby",
        "chad_darby@luv2code_school.com");
    List<CollegeStudent> collegeStudentList = new ArrayList<>(
        Arrays.asList(studentOne, studentTwo));

    when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

    assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

    MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
        .andExpect(status().isOk()).andReturn();

    ModelAndView mav = mvcResult.getModelAndView();

    ModelAndViewAssert.assertViewName(mav, "index");


  }

  @Test
  public void createStudentHttpRequest() throws Exception {
    CollegeStudent studentOne = new CollegeStudent("Eric", "Roby", "eric_roby@luv2code_shcool.com");

    List<CollegeStudent> collegeStudents = new ArrayList<>(Arrays.asList(studentOne));
    when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudents);

    assertIterableEquals(collegeStudents, studentCreateServiceMock.getGradebook());

    MvcResult mvcResult = this.mockMvc.perform(post("/")
        .contentType(MediaType.APPLICATION_JSON)
        .param("firstname", request.getParameterValues("firstname"))
        .param("lastname", request.getParameterValues("lastname"))
        .param("emailAddress", request.getParameterValues("emailAddress")))
        .andExpect(status().isOk()).andReturn();

    ModelAndView mav = mvcResult.getModelAndView();

    ModelAndViewAssert.assertViewName(mav, "index");

    CollegeStudent verifyStudent = studentDao.findByEmailAddress("chad_darby@luv2code_school.com");
    assertNotNull(verifyStudent, "Studnet should be found");


  }

  @Test
  public void deleteStudentHttopRequest() throws Exception {
    assertTrue(studentDao.findById(1).isPresent());

    MvcResult mvcResult = mockMvc.perform(get("/delete/student/{id}", 1)).andExpect(status().isOk())
        .andReturn();

    ModelAndView mav = mvcResult.getModelAndView();

    ModelAndViewAssert.assertViewName(mav, "index");

    assertFalse(studentDao.findById(1).isPresent());
  }

  @Test
  public void deleteStudentHttpRequestErrorPage() throws Exception {

    MvcResult mvcResult = mockMvc.perform(get("/delete/student/{id}", 0)).andExpect(status().isOk())
        .andReturn();

    ModelAndView mav = mvcResult.getModelAndView();

    ModelAndViewAssert.assertViewName(mav, "error");
  }


  @Test
  public void studentInformationHttpRequest() throws Exception {
    assertTrue(studentDao.findById(1).isPresent());

    MvcResult mvcResult = mockMvc.perform(get("/studentInformation/{id}", 1))
        .andExpect(status().isOk()).andReturn();

    ModelAndView mav = mvcResult.getModelAndView();

    ModelAndViewAssert.assertViewName(mav, "studentInformation");

  }

  @Test
  public void studentInformationHttpDoesNotExitsRequest() throws Exception {
    assertFalse(studentDao.findById(0).isPresent());

    MvcResult mvcResult = mockMvc.perform(get("/studentInformation/{id}", 0))
        .andExpect(status().isOk()).andReturn();

    ModelAndView mav = mvcResult.getModelAndView();

    ModelAndViewAssert.assertViewName(mav, "error");

  }

  @Test
  public void createValidGradeHttpRequest() throws Exception {
    assertTrue(studentDao.findById(1).isPresent());

    GradebookCollegeStudent student = studentService.studentInformation(1);

    assertEquals(1, student.getStudentGrades().getMathGradeResults().size());

    MvcResult mvcResult = this.mockMvc.perform(
        (post("/grades").contentType(MediaType.APPLICATION_JSON).param("grade", "85.00")
            .param("gradeType", "math").param("studentId", "1"))).andExpect(status().isOk())
        .andReturn();

    ModelAndView mav = mvcResult.getModelAndView();

    ModelAndViewAssert.assertViewName(mav, "studentInformation");
    student = studentService.studentInformation(1);

    assertEquals(2, student.getStudentGrades().getMathGradeResults().size());

  }

  @Test
  public void createAValidGradeHttpRequestStudentDoesNotExistEmptyResponse() throws Exception {
    MvcResult mvcResult = this.mockMvc.perform(
        (post("/grades").contentType(MediaType.APPLICATION_JSON).param("grade", "85.00")
            .param("gradeType", "history").param("studentId", "0"))).andExpect(status().isOk())
        .andReturn();
    ModelAndView mav = mvcResult.getModelAndView();

    ModelAndViewAssert.assertViewName(mav, "error");

  }


  @Test
  public void createAValidGradeHttpRequestGradeTypeDoesNotExistEmptyResponse() throws Exception {
    MvcResult mvcResult = this.mockMvc.perform(
        (post("/grades").contentType(MediaType.APPLICATION_JSON).param("grade", "85.00")
            .param("gradeType", "literature").param("studentId", "1"))).andExpect(status().isOk())
        .andReturn();
    ModelAndView mav = mvcResult.getModelAndView();

    ModelAndViewAssert.assertViewName(mav, "error");
  }

  @Test
  public void deleteAValidGradeHttpRequest() throws Exception {
    Optional<MathGrade> mathGrade = mathGradeDao.findById(1);
    assertTrue(mathGrade.isPresent());
    MvcResult mvcResult = mockMvc.perform(get("/grades/{id}/{gradeType}", 1, "math")).andExpect(
        status().isOk()).andReturn();

    ModelAndView mav = mvcResult.getModelAndView();
    ModelAndViewAssert.assertViewName(mav, "studentInformation");
    mathGrade = mathGradeDao.findById(1);
    assertFalse(mathGrade.isPresent());
  }

  @Test
  public void deleteAValidGradeHttpRequestStudentIdDoesNotExistEmptyResponse() throws Exception{
    Optional<MathGrade> mathGrade = mathGradeDao.findById(2);
    assertFalse(mathGrade.isPresent());
    MvcResult mvcResult = mockMvc.perform(get("/grades/{id}/{gradeType}", 2, "math")).andExpect(
        status().isOk()).andReturn();

    ModelAndView mav = mvcResult.getModelAndView();
    ModelAndViewAssert.assertViewName(mav, "error");
  }

  @Test
  public void deleteAValidGradeHttpRequestGradeTypeDoesNotExistEmptyResponse() throws Exception{

    MvcResult mvcResult = mockMvc.perform(get("/grades/{id}/{gradeType}", 1, "literature")).andExpect(
        status().isOk()).andReturn();

    ModelAndView mav = mvcResult.getModelAndView();
    ModelAndViewAssert.assertViewName(mav, "error");
  }

  @AfterEach

  public void setupAfterTransaction() {

    jdbc.execute(sqlDeleteStudent);
    jdbc.execute(sqlDeleteMathGrade);
    jdbc.execute(sqlDeleteScienceGrade);
    jdbc.execute(sqlDeleteHistoryGrade);
  }


}
