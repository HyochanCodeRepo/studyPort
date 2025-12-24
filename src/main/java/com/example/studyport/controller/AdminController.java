package com.example.studyport.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @PersistenceContext
    private final EntityManager entityManager;

    @GetMapping("/cleanup-categories")
    @Transactional
    public Map<String, Object> cleanupDuplicateCategories() {
        Map<String, Object> result = new HashMap<>();

        // 1. 중복 확인
        String checkQuery = "SELECT name, COUNT(*) as count FROM category GROUP BY name HAVING COUNT(*) > 1";
        List<Object[]> duplicates = entityManager.createNativeQuery(checkQuery).getResultList();

        result.put("duplicatesFound", duplicates.size());
        result.put("duplicates", duplicates);

        if (duplicates.isEmpty()) {
            result.put("message", "중복된 카테고리가 없습니다.");
            return result;
        }

        // 2. 중복 제거
        String deleteQuery = "DELETE c1 FROM category c1 " +
                           "INNER JOIN category c2 " +
                           "WHERE c1.name = c2.name " +
                           "AND c1.category_id > c2.category_id";

        int deletedCount = entityManager.createNativeQuery(deleteQuery).executeUpdate();
        result.put("deletedCount", deletedCount);

        // 3. 최종 결과
        String finalQuery = "SELECT category_id, name FROM category ORDER BY category_id";
        List<Object[]> finalCategories = entityManager.createNativeQuery(finalQuery).getResultList();

        result.put("finalCategories", finalCategories);
        result.put("message", deletedCount + "개의 중복 카테고리가 삭제되었습니다.");

        return result;
    }

    @GetMapping("/categories")
    public Map<String, Object> getAllCategories() {
        Map<String, Object> result = new HashMap<>();

        String query = "SELECT category_id, name FROM category ORDER BY category_id";
        List<Object[]> categories = entityManager.createNativeQuery(query).getResultList();

        result.put("total", categories.size());
        result.put("categories", categories);

        return result;
    }
}
