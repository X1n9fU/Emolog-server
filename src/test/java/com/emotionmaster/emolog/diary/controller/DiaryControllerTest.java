package com.emotionmaster.emolog.diary.controller;

import com.emotionmaster.emolog.diary.domain.Diary;
import com.emotionmaster.emolog.diary.dto.request.AddDiaryRequest;
import com.emotionmaster.emolog.diary.dto.request.Qa;
import com.emotionmaster.emolog.diary.repository.DiaryRepository;
import com.emotionmaster.emolog.emotion.domain.Emotion;
import com.emotionmaster.emolog.emotion.repository.EmotionRepository;
import com.emotionmaster.emolog.q_a.domain.Q_A;
import com.emotionmaster.emolog.q_a.repository.QaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest //test용 application context
@AutoConfigureMockMvc //mockmvc 생성 및 자동 구성
class DiaryControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    EmotionRepository emotionRepository;

    @Autowired
    QaRepository qaRepository;

    @BeforeEach
    public void setMockMvc(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        diaryRepository.deleteAll();
    }

    @DisplayName("saveDiary() : 전달받은 일기와 Q&A, 감정까지 모두 저장한다.")
    @Test
    public void saveDiary() throws Exception{
        //given
        final String url ="/api/diary";
        final LocalDate now = now();
        final String content = "content";
        final String emotion = "emotions";
        final String question = "questions";
        final String answer = "answers";
        final AddDiaryRequest request =
                new AddDiaryRequest(now, content, new Qa(question, answer), emotion);

        final String requestBody = objectMapper.writeValueAsString(request);

        //when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        //then
        //CREATED 인지, emotion, Q_A가 잘 들어갔는지
        result.andExpect(status().isCreated());

        List<Diary> diary = diaryRepository.findAll();
        List<Emotion> emotions = emotionRepository.findAll();
        List<Q_A> q_a = qaRepository.findAll();

        //개수 확인
        assertThat(diary.size()).isEqualTo(1);
        assertThat(emotions.size()).isEqualTo(1);
        assertThat(q_a.size()).isEqualTo(1);

        //내용 확인
        assertThat(diary.get(0).getDate()).isEqualTo(now);
        assertThat(diary.get(0).getContent()).isEqualTo(content);
        assertThat(emotions.get(0).getEmotion()).isEqualTo(emotion);
        assertThat(q_a.get(0).getQuestion()).isEqualTo(question);
        assertThat(q_a.get(0).getAnswer()).isEqualTo(answer);

        //join 확인
        assertThat(emotions.get(0).getDiary().getId()).isEqualTo(diary.get(0).getId());
        assertThat(q_a.get(0).getDiary().getId()).isEqualTo(diary.get(0).getId());

    }

    @DisplayName("deleteDiary() : id 값에 맞춰 일기를 삭제한다")
    @Test
    public void deleteDiary() throws Exception {
        final String url = "/api/diary/{id}";
        Diary diary = diaryRepository.save(new Diary(LocalDate.now(), "content"));

        ResultActions results = mockMvc.perform(delete(url,diary.getId()));

        List<Diary> diaryList = diaryRepository.findAll();

        results.andExpect(status().isOk());

        assertThat(diaryList.size()).isZero();

    }
}