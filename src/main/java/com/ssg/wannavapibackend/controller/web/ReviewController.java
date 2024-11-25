package com.ssg.wannavapibackend.controller.web;

import com.ssg.wannavapibackend.domain.Restaurant;
import com.ssg.wannavapibackend.dto.request.ReviewSaveDTO;
import com.ssg.wannavapibackend.dto.response.OCRResponseDTO;
import com.ssg.wannavapibackend.service.OCRService;
import com.ssg.wannavapibackend.service.ReviewService;
import com.ssg.wannavapibackend.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;
    private final OCRService ocrService;
    private final TagService tagService;

    @GetMapping("upload-receipt")
    public String uploadReceipt() {
        return "review/receipt";
    }

    @PostMapping("upload-receipt")
    public String ProcessReceipt(@RequestParam("file") MultipartFile file, Model model,
                                 RedirectAttributes redirectAttributes) throws IOException {
        OCRResponseDTO responseDTO = ocrService.findReceiptData(file);

        //영수증 처리 불가
        if (responseDTO.getImages().get(0).getReceipt() == null
                || responseDTO.getImages().get(0).getReceipt().getResult() == null
                || responseDTO.getImages().get(0).getReceipt().getResult().getStoreInfo() == null
                || responseDTO.getImages().get(0).getReceipt().getResult().getStoreInfo().getBizNum() == null
                || responseDTO.getImages().get(0).getReceipt().getResult().getPaymentInfo() == null) {
            log.info("영수증 처리 불가");
            model.addAttribute("alertMessage", "영수증이 아니거나 식당명을 확인할 수 없습니다.");
            return "review/receipt";
        }

        OCRResponseDTO.StoreInfo storeInfo = responseDTO.getImages().get(0).getReceipt().getResult().getStoreInfo();
        Restaurant restaurant = ocrService.findCorrectRestaurant(storeInfo);

        //목록에 없는 식당 처리 불가
        if (restaurant == null) {
            log.info("목록에 없는 식당: {}", storeInfo.getName().getText());
            model.addAttribute("alertMessage", "목록에 없는 식당입니다.");
            return "review/receipt";
        }

        //정상 처리
        redirectAttributes.addFlashAttribute("restaurant", restaurant);
        redirectAttributes.addFlashAttribute("visitDate", ocrService.findCorrectVisitDate(responseDTO.getImages().get(0).getReceipt().getResult().getPaymentInfo().getDate().getText()));
        return "redirect:/reviews/write";
    }

    @GetMapping("reviews/write")
    public String saveReview(Model model) {
        Restaurant restaurant = (Restaurant) model.getAttribute("restaurant");
        LocalDate visitDate = (LocalDate) model.getAttribute("visitDate");

        if (restaurant == null || visitDate == null) {
            log.info("리뷰 작성 GET 요청 방지");
            model.addAttribute("alertMessage", "영수증을 먼저 인식해주세요.");
            return "review/receipt";
        }

        model.addAttribute("reviewSaveDTO", ReviewSaveDTO.builder().restaurant(restaurant).visitDate(visitDate).build());
        model.addAttribute("tagsAll", tagService.findTagsForReview());
        return "review/review-write";
    }

    @PostMapping("reviews/write")
    public String saveReview(@ModelAttribute @Validated ReviewSaveDTO reviewSaveDTO, BindingResult bindingResult,
                             Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            printErrorLog(bindingResult);
            model.addAttribute("reviewSaveDTO", reviewSaveDTO);
            model.addAttribute("tagsAll", tagService.findTagsForReview());
            return "review/review-write";
        }
        reviewService.saveReview(1L, reviewSaveDTO);
        log.info("리뷰 작성 완료");
        redirectAttributes.addFlashAttribute("alertMessage", "작성 완료되었습니다.");
        return "redirect:/reviews";
    }

    @PostMapping("reviews/{id}/edit")
    public String updateReview(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            reviewService.checkReviewUpdate(id);

            if (id == null) {
                log.info("리뷰 정보를 찾을 수 없습니다.");
                return "redirect:/reviews";
            }
            model.addAttribute("reviewInfo", reviewService.findReview(id));
            model.addAttribute("reviewUpdateDTO", reviewService.findReview(id));
            model.addAttribute("tagsAll", tagService.findTagsForReview());
            return "review/review-update";
        } catch (Exception e) {
            log.info("리뷰 수정 불가");
            redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
            return "redirect:/reviews";
        }
    }

    @PostMapping("reviews/{id}/update")
    public String processReview(@PathVariable Long id, @ModelAttribute @Validated ReviewSaveDTO reviewUpdateDTO, BindingResult bindingResult,
                                Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            printErrorLog(bindingResult);
            model.addAttribute("reviewUpdateDTO", reviewUpdateDTO);
            model.addAttribute("tagsAll", tagService.findTagsForReview());
            return "review/review-update";
        }
        reviewService.updateReview(id, reviewUpdateDTO);
        log.info("리뷰 수정 완료");
        redirectAttributes.addFlashAttribute("alertMessage", "수정 되었습니다.");
        return "redirect:/reviews";
    }

    @PostMapping("reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reviewService.deleteReview(id);
            log.info("{}번 리뷰 삭제 완료", id);
            redirectAttributes.addFlashAttribute("alertMessage", "삭제 되었습니다.");
            return "redirect:/reviews";
        } catch (Exception e) {
            log.info("리뷰 삭제 불가");
            redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
            return "redirect:/reviews";
        }
    }

    private static void printErrorLog(BindingResult result) {
        log.info("{}", "*".repeat(20));
        for (FieldError fieldError : result.getFieldErrors()) {
            log.error("{}: {}", fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.info("{}", "*".repeat(20));
    }
}