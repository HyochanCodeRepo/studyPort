package com.example.studyport.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 글로벌 예외 처리 핸들러
 * 404, 500 등의 에러를 처리하고 사용자 정의 에러 페이지로 연결
 */
@Controller
@Log4j2
@RequestMapping("/error")
public class GlobalExceptionHandler implements ErrorController {

    /**
     * 404 Not Found 처리
     */
    @GetMapping("/404")
    public String handle404(Model model, HttpServletRequest request) {
        log.warn("===========================================");
        log.warn("404 Not Found Error");
        log.warn("요청 경로: {}", request.getRequestURI());
        log.warn("===========================================");
        
        model.addAttribute("timestamp", System.currentTimeMillis());
        model.addAttribute("status", 404);
        model.addAttribute("message", "페이지를 찾을 수 없습니다");
        
        return "error/404";
    }

    /**
     * 500 Internal Server Error 처리
     */
    @GetMapping("/500")
    public String handle500(Model model, HttpServletRequest request) {
        log.error("===========================================");
        log.error("500 Internal Server Error");
        log.error("요청 경로: {}", request.getRequestURI());
        log.error("===========================================");
        
        model.addAttribute("timestamp", System.currentTimeMillis());
        model.addAttribute("status", 500);
        model.addAttribute("message", "서버 오류가 발생했습니다");
        
        return "error/500";
    }

    /**
     * 기본 에러 페이지 (status code에 따라 처리)
     */
    @RequestMapping
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute("javax.servlet.error.status_code");
        Object exception = request.getAttribute("javax.servlet.error.exception");
        
        log.error("===========================================");
        log.error("에러 발생");
        log.error("Status: {}", status);
        log.error("Exception: {}", exception);
        log.error("요청 경로: {}", request.getRequestURI());
        log.error("===========================================");
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            model.addAttribute("status", statusCode);
            model.addAttribute("timestamp", System.currentTimeMillis());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("message", "페이지를 찾을 수 없습니다");
                return "error/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("message", "서버 오류가 발생했습니다");
                return "error/500";
            }
        }
        
        // 기본 500 에러 페이지
        return "error/500";
    }

    /**
     * 404 - NoHandlerFoundException 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoHandlerFound(NoHandlerFoundException e, Model model) {
        log.warn("===========================================");
        log.warn("NoHandlerFoundException");
        log.warn("요청된 경로: {}", e.getRequestURL());
        log.warn("===========================================");
        
        model.addAttribute("timestamp", System.currentTimeMillis());
        model.addAttribute("status", 404);
        model.addAttribute("message", "페이지를 찾을 수 없습니다");
        
        return "error/404";
    }

    /**
     * 500 - Exception 처리
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model, HttpServletRequest request) {
        log.error("===========================================");
        log.error("예기치 않은 예외 발생");
        log.error("요청 경로: {}", request.getRequestURI());
        log.error("예외 메시지: {}", e.getMessage());
        log.error("===========================================", e);
        
        model.addAttribute("timestamp", System.currentTimeMillis());
        model.addAttribute("status", 500);
        model.addAttribute("message", "서버 오류가 발생했습니다");
        
        return "error/500";
    }


}
