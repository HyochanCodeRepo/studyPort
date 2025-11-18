package com.example.studyport.controller;

import com.example.studyport.dto.MembersDTO;
import com.example.studyport.entity.Category;
import com.example.studyport.service.CategoryService;
import com.example.studyport.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/members")
public class testcontroller {

    private final MemberService memberService;
    private final CategoryService categoryService;

    @PersistenceContext
    private final EntityManager entityManager;

    @GetMapping("/signup01")
    public String signupGet01(MembersDTO membersDTO, HttpSession session, Model model) {


        // ⭐️ 카테고리 목록을 모델에 추가
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        return "members/singup01";
    }

    @GetMapping("/signup02")
    public String signupGet02(MembersDTO membersDTO, HttpSession session, Model model) {


        // ⭐️ 카테고리 목록을 모델에 추가
        List<Category> categories = categoryService.findAll();
        model.addAttribute("categories", categories);

        return "members/signup02";
    }

    @GetMapping("/signup03")
    public String signupGet03() {

        return "members/signup03";
    }
    @GetMapping("/signup04")
    public String signupGet04() {

        return "members/signup04";
    }

    @GetMapping("/cleanup-categories")
    @ResponseBody
    @Transactional
    public Map<String, Object> cleanupCategories() {
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


}
