/***********************************************
 * 인터페이스명 : LoginUser
 * 기능 :
 * 작성자 :
 * 작성일 : 2025-07-16
 * 수정 : 2025-07-16
 * ***********************************************/
package com.example.studyport.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
