package com.sparta.myselectshop.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
// @MappedSuperclass 객체의 입장에서 공통 매핑 정보가 필요할 때 사용한다.
// id, name 은 객체의 입장에서 볼 때 계속 나온다. 이렇게 공통 매핑 정보가 필요할 때,
// 부모 클래스에 선언하고 속성만 상속 받아서 사용 하고 싶을 때 사용한다.
@EntityListeners(AuditingEntityListener.class)
// 엔티티의 변화를 감지하고 테이블의 데이터를 조작하는 일을 한다.
// 해당 클래스에 Auditing 기능을 포함한다.

// 참고 링크 : https://devlog-wjdrbs96.tistory.com/415
public class Timestamped {

    // JPA Auditing 애노테이션들이 활성화할 수 있도록 @EnableJpaAuditing 애노테이션을 위와 같이 추가
    // 실행부인 어플리케이션에 @EnableJpaAuditing 를 추가 하면 아래의
    // @CreatedDate, @LastModifiedDate 사용이 가능하다.
    @CreatedDate        // 데이터를 저장할 때 생성된 시간 정보 자동 저장
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate   // 데이터를 저장할 때 수정된 시간 정보 자동 저장
    @Column
    private LocalDateTime modifiedAt;
}