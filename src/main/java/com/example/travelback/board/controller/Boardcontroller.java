package com.example.travelback.board.controller;


import com.example.travelback.board.domain.Board;
import com.example.travelback.board.service.BoardService;
import com.example.travelback.user.dto.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")

public class Boardcontroller {

    private  final BoardService service;

    @GetMapping("list")
    public Map<String, Object> list(@RequestParam(value = "p" ,defaultValue = "1")Integer page,
                                    @RequestParam(value = "k" ,defaultValue = "")String keyword){
        return service.list(page,keyword);
    }





    @PostMapping("add")
    public ResponseEntity add( Board board,
                               @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] files,
                              @SessionAttribute(value = "login" ,required = false)Member login) throws IOException {
        // 로그인 여부확인
        if(login==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 검증하기
        if (!service.validate(board)){
            return ResponseEntity.badRequest().build();
        }

        // 글 추가 하기
        if (service.add(board,files,login)){
        return     ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.internalServerError().build();
        }
    }



    @GetMapping("id/{id}")
    public Board getId(@PathVariable Integer id){
        return  service.id(id);
    }


    @DeleteMapping("remove/{id}")
    public ResponseEntity remove(@PathVariable Integer id,
                                 @SessionAttribute(value = "login",required = false)Member login){

        //401 권한 없음 에러
        if (login==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //403 서버에서 거절
        if (!service.hasAccess(id,login)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


         if (service.remove(id)){
             return ResponseEntity.ok().build();
         }else {
             return ResponseEntity.internalServerError().build();
         }
    }

    @PutMapping("edit")
    public  ResponseEntity update(
            @RequestBody Board board, @SessionAttribute (value = "login",required = false)Member login){

        if (login==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!service.hasAccess(board.getId(),login)){
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (service.update(board)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }

    }

















}
