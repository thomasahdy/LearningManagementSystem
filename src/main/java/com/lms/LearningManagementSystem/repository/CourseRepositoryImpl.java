package com.lms.LearningManagementSystem.repository;

import com.lms.LearningManagementSystem.model.Course;
import com.lms.LearningManagementSystem.repository.CourseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public class CourseRepositoryImpl implements CourseRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Course> findByInstructorUsername(String instructorUsername) {
        return entityManager.createQuery(
                        "SELECT c FROM Course c WHERE c.instructor.username = :username", Course.class)
                .setParameter("username", instructorUsername)
                .getResultList();
    }

    // Find courses where a student is enrolled (based on username)
    @Override
    public List<Course> findByEnrolledStudentsContaining(String username) {
        return entityManager.createQuery(
                        "SELECT c FROM Course c WHERE :username MEMBER OF c.enrolledStudents", Course.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Course> findByInstructorId(Long instructorId) {
        return entityManager.createQuery(
                "SELECT c FROM Course c WHERE c.instructor.id = :instructorId", Course.class)
                .setParameter("instructorId", instructorId)
                .getResultList();
    }

    @Override
    public <S extends Course> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Course> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Course> findAll() {
        return entityManager.createQuery("SELECT c FROM Course c", Course.class)
                .getResultList();
    }

    @Override
    public List<Course> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public Course save(Course course) {
        if (course.getId() == null) {
            entityManager.persist(course);
            return course;
        } else {
            return entityManager.merge(course);
        }
    }

    @Override
    public void deleteById(Long id) {
        Course course = entityManager.find(Course.class, id);
        if (course != null) {
            entityManager.remove(course);
        }
    }

    @Override
    public void delete(Course entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Course> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Course> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Course> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Course> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Course getOne(Long aLong) {
        return null;
    }

    @Override
    public Course getById(Long id) {
        return entityManager.find(Course.class, id);
    }

    @Override
    public Course getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Course> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Course> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Course> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Course> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Course> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Course> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Course, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }



    @Override
    public List<Course> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Course> findAll(Pageable pageable) {
        return null;
    }
}