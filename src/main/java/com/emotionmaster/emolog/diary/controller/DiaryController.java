package com.emotionmaster.emolog.diary.controller;

import com.emotionmaster.emolog.diary.domain.Diary;
import com.emotionmaster.emolog.diary.dto.request.AddDiaryRequest;
import com.emotionmaster.emolog.diary.dto.response.AddDiaryResponse;
import com.emotionmaster.emolog.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<AddDiaryResponse> saveDiary(@RequestBody AddDiaryRequest request){
        Diary savedDiary = diaryService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AddDiaryResponse(savedDiary));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable("id") long id){
        diaryService.delete(id);
        return ResponseEntity.ok()
                .build();
    }

    @GetMapping
    public ResponseEntity<List<Map<LocalDate, String>>> findColorByMonth(@RequestParam("month") int month){
        List<Map<LocalDate, String>> colorList = diaryService.findAllColorOfMonth(month)
                .stream()
                .map(this::dateAndColor)
                .toList();

        return ResponseEntity.ok()
                .body(colorList);
    }

    private Map<LocalDate, String> dateAndColor(Diary diary){
        return Map.of(diary.getDate(), diary.getColor().getHexa());
    }
}
