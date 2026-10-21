package com.example.demo.controller;

import java.net.URI;
import java.util.List;
// import java.util.Map; // [7주차 연습문제 ②] try-catch 이동으로 사용하지 않음 (ApiExceptionHandler 에서 사용)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.domain.Member;
import com.example.demo.model.dto.MemberForm;
import com.example.demo.model.dto.MemberResponse;
import com.example.demo.model.dto.RoleRequest;
import com.example.demo.model.service.MemberService;

@RestController // 관리자 회원 관리 API (JSON)
@RequestMapping("/api/admin/members") // 주소는 하나, 할 일은 메서드로 구분
public class AdminApiController {
    @Autowired
    MemberService memberService;

    // [7주차 연습문제 ①] 변경 : ?keyword= 검색 추가 (아래 새 list 메서드)
    // @GetMapping // 회원 목록 : GET /api/admin/members
    // public List<MemberResponse> list() {
    //     List<Member> members = memberService.findAll();
    //     return members.stream().map(MemberResponse::from).toList(); // 엔티티 목록 → DTO 목록
    // }

    // [7주차 연습문제 ①] 회원 목록 + 검색 : GET /api/admin/members?keyword=student
    @GetMapping
    public List<MemberResponse> list(@RequestParam(required = false) String keyword) { // 없으면 null → 전체
        List<Member> members = memberService.search(keyword);
        return members.stream().map(MemberResponse::from).toList(); // 엔티티 목록 → DTO 목록
    }

    @GetMapping("/{id}") // 회원 1명 : GET /api/admin/members/3
    public ResponseEntity<MemberResponse> get(@PathVariable Long id) {
        return memberService.findById(id)
                .map(member -> ResponseEntity.ok(MemberResponse.from(member))) // 있으면 200 OK
                .orElse(ResponseEntity.notFound().build()); // 없으면 404
    }

    // [7주차 연습문제 ②] 변경 : 반복되는 try-catch 를 ApiExceptionHandler(@RestControllerAdvice) 로 이동
    // @PostMapping // 회원 등록 : POST /api/admin/members (본문 JSON → MemberForm)
    // public ResponseEntity<?> create(@RequestBody MemberForm form) {
    //     try {
    //         Member saved = memberService.signup(form); // 5주차 회원가입 로직 재사용 (BCrypt)
    //         return ResponseEntity
    //                 .created(URI.create("/api/admin/members/" + saved.getId())) // 201 + Location
    //                 .body(MemberResponse.from(saved));
    //     } catch (IllegalArgumentException e) { // 중복 아이디, 빈 값 …
    //         return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); // 400
    //     }
    // }
    //
    // @PatchMapping("/{id}/role") // 권한 변경 : PATCH /api/admin/members/3/role {"role":"ADMIN"}
    // public ResponseEntity<?> changeRole(@PathVariable Long id, @RequestBody RoleRequest request) {
    //     try {
    //         Member member = memberService.changeRole(id, request.role());
    //         return ResponseEntity.ok(MemberResponse.from(member)); // 200 OK + 바뀐 회원
    //     } catch (IllegalArgumentException e) { // 허용되지 않는 권한, 없는 회원 …
    //         return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    //     }
    // }
    //
    // @DeleteMapping("/{id}") // 회원 삭제 : DELETE /api/admin/members/3
    // public ResponseEntity<?> delete(@PathVariable Long id) {
    //     try {
    //         memberService.deleteMember(id);
    //         return ResponseEntity.noContent().build(); // 204 No Content (돌려줄 본문 없음)
    //     } catch (IllegalArgumentException e) {
    //         return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    //     }
    // }

    // [7주차 연습문제 ②] try-catch 제거 : IllegalArgumentException 은 ApiExceptionHandler 가 400 으로 응답
    @PostMapping // 회원 등록 : POST /api/admin/members (본문 JSON → MemberForm)
    public ResponseEntity<MemberResponse> create(@RequestBody MemberForm form) {
        Member saved = memberService.signup(form); // 5주차 회원가입 로직 재사용 (BCrypt)
        return ResponseEntity
                .created(URI.create("/api/admin/members/" + saved.getId())) // 201 + Location
                .body(MemberResponse.from(saved));
    }

    @PatchMapping("/{id}/role") // 권한 변경 : PATCH /api/admin/members/3/role {"role":"ADMIN"}
    public ResponseEntity<MemberResponse> changeRole(@PathVariable Long id, @RequestBody RoleRequest request) {
        Member member = memberService.changeRole(id, request.role());
        return ResponseEntity.ok(MemberResponse.from(member)); // 200 OK + 바뀐 회원
    }

    @DeleteMapping("/{id}") // 회원 삭제 : DELETE /api/admin/members/3
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build(); // 204 No Content (돌려줄 본문 없음)
    }

}
