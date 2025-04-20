package com.thacbao.codeSphere.services.sandbox;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.data.repository.exercise.SubmissionRepository;
import com.thacbao.codeSphere.data.repository.exercise.TestCaseHistoryRepo;
import com.thacbao.codeSphere.data.repository.exercise.TestCaseRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.dto.request.sandbox.SandBoxRequest;
import com.thacbao.codeSphere.dto.request.sandbox.SubmissionRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.sandbox.FinalResponse;
import com.thacbao.codeSphere.dto.response.sandbox.SubmissionResponse;
import com.thacbao.codeSphere.dto.response.sandbox.TestCaseResponse;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.SubmissionHistory;
import com.thacbao.codeSphere.entities.reference.TestCase;
import com.thacbao.codeSphere.entities.reference.TestCaseHistory;
import com.thacbao.codeSphere.services.SandBoxService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SandBoxServiceImpl implements SandBoxService {

    @Value("${sandbox.api.apiUrl}")
    private String sandBoxUrl;

    @Value("${sandbox.api.apiKey}")
    private String sandBoxApiKey;

    private final TestCaseRepository testCaseRepository;
    private final SubmissionRepository submissionRepository;
    private final TestCaseHistoryRepo testCaseHistoryRepo;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    /**
     * tạo 1 submission và gọi đến hàm getsubmission lấy kết quả, thực hiện lưu lịch sử submit
     * @param submissionRequest
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> createSubmission(SubmissionRequest submissionRequest) {

        try {
            List<TestCase> testCases = testCaseRepository.findByExerciseId(submissionRequest.getExerciseId());
            ArrayList<TestCaseResponse> testCaseResponses = new ArrayList<>();

            User user = getUser();
            SubmissionHistory submissionHistory = new SubmissionHistory();
            submissionHistory.setExercise(testCases.get(0).getExercise());
            submissionHistory.setUser(user);
            submissionHistory.setSourceCode(submissionRequest.getSourceCode());
            submissionHistory.setCreatedAt(LocalDate.now());
            SubmissionHistory submissionHistorySaved = submissionRepository.save(submissionHistory);

            int passed = 0;
            ArrayList<TestCaseHistory> testCaseHistories = new ArrayList<>();
            for (TestCase testCase : testCases) {
                HttpEntity<SandBoxRequest> requestHttpEntity = getJudge0Request(submissionRequest.getSourceCode(),
                        submissionRequest.getLanguageId(), testCase.getInput(), testCase.getOutput());

                ResponseEntity<Map> responseEntity = restTemplate.exchange(sandBoxUrl
                                + "/submissions?base64_encoded=true&wait=true&fields=*"
                        , HttpMethod.POST, requestHttpEntity, Map.class);

                String token = responseEntity.getBody().get("token").toString();

                SubmissionResponse submissionResponse = getSubmission(token);

                boolean passedStatus = compareOutput(submissionResponse.getStdout(), testCase.getOutput());
                if (passedStatus) {
                        passed++;
                }
                TestCaseHistory testCaseHistory = TestCaseHistory.builder()
                        .passed(passedStatus)
                        .testCaseExpected(testCase.getOutput())
                        .output(submissionResponse.getStdout())
                        .statusId(submissionResponse.getStatusId())
                        .statusName(submissionResponse.getStatusName())
                        .time(submissionResponse.getTime())
                        .memory(submissionResponse.getMemory())
                        .errorMessage(submissionResponse.getErrorMessage())
                        .submissionHistory(submissionHistorySaved)
                        .build();
                    testCaseHistories.add(testCaseHistory);
                // tao ket qua cho tung test
                testCaseResponses.add(new TestCaseResponse(
                        testCase.getId(),
                        passedStatus,
                        testCase.getOutput(),
                        submissionResponse.getStdout(),
                        submissionResponse
                ));
            }
            int score = (int) Math.round((double) passed/ testCases.size() *100);
            String status = "Error";
            if (passed == testCases.size()) {
                status = "Accepted";
            }
            FinalResponse finalResponse = new FinalResponse(
                    status ,passed, score, testCases.size(), testCaseResponses
            );

            submissionHistorySaved.setStatus(status);
            submissionHistorySaved.setPassCount(passed);
            submissionHistorySaved.setScore(score);
            submissionHistorySaved.setTotalTestCases(testCases.size());
            submissionRepository.save(submissionHistorySaved);

            testCaseHistoryRepo.saveAll(testCaseHistories);

            return CodeSphereResponses.generateResponse(finalResponse, "Submission and score success", HttpStatus.OK);
        }
        catch (Exception e) {
            return CodeSphereResponses.generateResponse(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * Convert sang judge0 request để gọi api
     * @param sourceCode
     * @param languageId
     * @param input
     * @param output
     * @return
     */
    private HttpEntity<SandBoxRequest> getJudge0Request(String sourceCode, Integer languageId, String input, String output) {
        SandBoxRequest sandBoxRequest = new SandBoxRequest();
        sandBoxRequest.setSource_code(Base64.getEncoder().encodeToString(sourceCode.getBytes()));
        sandBoxRequest.setLanguage_id(languageId);
        String encodedInput = (input != null) ?
                Base64.getEncoder().encodeToString(input.getBytes()) :
                Base64.getEncoder().encodeToString("".getBytes());
        sandBoxRequest.setStdin(encodedInput);

        String encodedOutput = (output != null) ?
                Base64.getEncoder().encodeToString(output.getBytes()) :
                Base64.getEncoder().encodeToString("".getBytes());
        sandBoxRequest.setExpected_output(encodedOutput);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-RapidAPI-Key", sandBoxApiKey);
        return new HttpEntity<>(sandBoxRequest, headers);
    }

    /**
     * Lấy kết quả qua param token tạo từ rq trên
     * @param token
     * @return
     */
    private SubmissionResponse getSubmission(String token){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-RapidAPI-Key", sandBoxApiKey);
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> responseEntity = restTemplate.exchange(
                    sandBoxUrl + "/submissions/" + token + "?base64_encoded=true&fields=*",
                    HttpMethod.GET,
                    entity,
                    Map.class);

            Map<String, Object> responseBody = responseEntity.getBody();

            SubmissionResponse submissionResponse = new SubmissionResponse();
            submissionResponse.setToken(responseBody.get("token").toString());
            submissionResponse.setTime(Double.parseDouble((String) responseBody.get("time")));
            submissionResponse.setMemory(Long.parseLong(responseBody.get("memory").toString()));
            if (responseBody.get("status") != null) {
                Map<String, Object> status = (Map<String, Object>) responseBody.get("status");
                submissionResponse.setStatusId(((Number) status.get("id")).intValue());
                submissionResponse.setStatusName((String) status.get("description"));
            }
            if (responseBody.get("stderr") != null) {
                String stderr = new String(Base64.getDecoder().decode((String) responseBody.get("stderr")));
                submissionResponse.setErrorMessage(stderr);
            }

            if (responseBody.get("stdout") != null) {
                String base64Data = (String) responseBody.get("stdout");
                base64Data = base64Data.trim();
                byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
                String stdout = new String(decodedBytes, "UTF-8");
                submissionResponse.setStdout(stdout);
            }
            else {
                submissionResponse.setStdout("");
            }
            return submissionResponse;
        }
        catch (NullPointerException e) {
            log.error("NullPointerException in getSubmission: {}", e.getMessage());
            SubmissionResponse fallbackResponse = new SubmissionResponse();
            fallbackResponse.setStdout("");
            return fallbackResponse;
        }
        catch (Exception e){
            log.error("Error logging get submission {}", e.getMessage(), e.getCause());
            throw new RuntimeException(e);
        }
    }

    private boolean compareOutput(String actual, String expected) {
        String normarlizeActual = normalizeOutput(actual);
        String normarlizeExpected = normalizeOutput(expected);
        return normarlizeActual.equals(normarlizeExpected);
    }

    private String normalizeOutput(String output) {
        if (output == null) {
            return "";
        }
        else return output.trim();
    }

    private User getUser() {
        return userDetailsService.getUserDetails();
    }
}
