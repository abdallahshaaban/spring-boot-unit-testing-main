package com.luv2code.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.dao.ApplicationDao;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import com.luv2code.component.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class MockAnnotationTest {

  @Autowired
  ApplicationContext context;

  @Autowired
  CollegeStudent studentOne;

  @Autowired
  StudentGrades studentGrades;

  @Mock
  private ApplicationDao applicationDao;

  @InjectMocks
  private ApplicationService applicationService;

  @BeforeEach
  public void beforeEach() {
    studentOne.setFirstname("Eric");
    studentOne.setLastname("Roby");
    studentOne.setEmailAddress("eric.roby@luv2code_school.com");
    studentOne.setStudentGrades(studentGrades);

  }

  @DisplayName("When & Verify")
  @Test
  public void assertEqualsTestAddGrades() {
    when(applicationDao.addGradeResultsForSingleClass(studentGrades.getMathGradeResults()))
        .thenReturn(100.0);
    assertEquals(100, applicationService
        .addGradeResultsForSingleClass(studentOne.getStudentGrades().getMathGradeResults()));

    verify(applicationDao).addGradeResultsForSingleClass(studentGrades.getMathGradeResults());

    verify(applicationDao, times(1))
        .addGradeResultsForSingleClass(studentGrades.getMathGradeResults());

  }

}
