package uth.edu.vn.ccmarket.controller;

import uth.edu.vn.ccmarket.model.EVOwner;
import uth.edu.vn.ccmarket.model.Trip;
import uth.edu.vn.ccmarket.repository.EVOwnerRepository;
import uth.edu.vn.ccmarket.service.TripProcessorService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/trips")
public class TripController {

    private final TripProcessorService tripService;
    private final EVOwnerRepository ownerRepo;

    public TripController(TripProcessorService tripService, EVOwnerRepository ownerRepo) {
        this.tripService = tripService;
        this.ownerRepo = ownerRepo;
    }

    @PostMapping("/upload")
    public String handleFileUpload(
            @RequestParam("file") MultipartFile file, // (4) Lấy file từ form
            @AuthenticationPrincipal UserDetails userDetails, // (5) Lấy người dùng đang login
            RedirectAttributes redirectAttributes) { // (6)

        EVOwner owner = ownerRepo.findByUsername(userDetails.getUsername());
        if (owner == null) {

            redirectAttributes.addFlashAttribute("message", "Lỗi: Không tìm thấy người dùng!");
            return "redirect:/login";
        }

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Lỗi: File không được rỗng!");
            return "redirect:/dashboard";
        }

        try {

            List<Trip> trips = tripService.parseTripsCsv(file.getInputStream(), owner);

            tripService.createCreditFromTrips(owner, trips);

            String successMsg = "Tải lên thành công " + trips.size()
                    + " chuyến đi. Tín chỉ (chưa xác minh) đã được tạo.";
            redirectAttributes.addFlashAttribute("message", successMsg);

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute("message", "Upload thất bại: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }
}