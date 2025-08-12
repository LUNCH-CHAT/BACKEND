package com.lunchchat.domain.member.service;

import com.lunchchat.domain.member.exception.MemberException;
import com.lunchchat.global.apiPayLoad.code.status.ErrorStatus;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class AiKeywordService {

  private final ChatClient chatClient;

  public AiKeywordService(ChatClient.Builder chatClientBuilder){
    this.chatClient=chatClientBuilder.build();
  }

  public String generateKeyword(String description) {

    // 기본 검증
    if (description == null || description.trim().isEmpty()) {
      throw new MemberException(ErrorStatus.KEYWORD_DESC_REQUIRED);
    }

    String input = description.length() > 800 ? description.substring(0, 800) : description;

    // 시스템 프롬프트
    String systemPrompt = """
        You are a keyword generator.
        Based on the user's description, output exactly one keyword in Korean.
        Output rules:
        - Output: just the keyword text, no explanations.
        - Do NOT include the word 'keyword' or any prefix before the keyword.
        - The keyword must be a single word or hyphenated phrase.
        - Example: "예술인", not "keyword예술인"
        Examples:
        Description: "I like playing football and running."
        Keyword: "스포츠인"
        Description: "I enjoy painting and visiting art galleries."
        Keyword: "예술인"
        """;

    OpenAiChatOptions options = OpenAiChatOptions.builder()
        .temperature(0.0)
        .maxCompletionTokens(32)
        .build();

    String result = chatClient
        .prompt()
        .system(systemPrompt)
        .user("Description: " + input)
        .options(options)
        .call()
        .content()
        .trim();

    if (result.toLowerCase().startsWith("keyword")) {
      result = result.substring(7).trim();
    }

    return getNormalized(result);
  }

  private String getNormalized(String raw){
    // 정규화
    String normalized = raw
        .toLowerCase()
        .replaceAll("[\"'`]", "")  // 따옴표 제거
        .replaceAll("\\s+", "")    // 공백 제거
        .replaceAll("[^가-힣a-zA-Z0-9]", "");  // 한글, 영문, 숫자만 유지

    return normalized;
  }

}
