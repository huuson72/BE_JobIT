package vn.hstore.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hstore.jobhunter.domain.Subscriber;
import vn.hstore.jobhunter.service.SubscriberService;
import vn.hstore.jobhunter.util.SecurityUtil;
import vn.hstore.jobhunter.util.annotation.ApiMessage;
import vn.hstore.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    // @PostMapping("/subscribers")
    // @ApiMessage("Create a subscriber")
    // public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber sub) throws IdInvalidException {
    //     // check email
    //     boolean isExist = this.subscriberService.isExistsByEmail(sub.getEmail());
    //     if (isExist == true) {
    //         throw new IdInvalidException("Email " + sub.getEmail() + " đã tồn tại");
    //     }
    //     return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(sub));
    // }
    @PostMapping("/subscribers")
    @ApiMessage("Create a subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber sub) throws IdInvalidException {
        // check email
        boolean isExist = this.subscriberService.isExistsByEmail(sub.getEmail());
        if (isExist) {
            throw new IdInvalidException("Email " + sub.getEmail() + " đã tồn tại");
        }

        Subscriber newSub = this.subscriberService.create(sub);

        // 📧 Gửi email khi đăng ký subscriber mới
        this.subscriberService.sendSubscribersEmailJobsForUser(newSub);

        return ResponseEntity.status(HttpStatus.CREATED).body(newSub);
    }

    // @PutMapping("/subscribers")
    // @ApiMessage("Update a subscriber")
    // public ResponseEntity<Subscriber> update(@RequestBody Subscriber subsRequest) throws IdInvalidException {
    //     // check id
    //     Subscriber subsDB = this.subscriberService.findById(subsRequest.getId());
    //     if (subsDB == null) {
    //         throw new IdInvalidException("Id " + subsRequest.getId() + " không tồn tại");
    //     }
    //     return ResponseEntity.ok().body(this.subscriberService.update(subsDB, subsRequest));
    // }
    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber")
    public ResponseEntity<Subscriber> update(@RequestBody Subscriber subsRequest) throws IdInvalidException {
        // check id
        Subscriber subsDB = this.subscriberService.findById(subsRequest.getId());
        if (subsDB == null) {
            throw new IdInvalidException("Id " + subsRequest.getId() + " không tồn tại");
        }

        Subscriber updatedSub = this.subscriberService.update(subsDB, subsRequest);

        // 📧 Gửi email khi subscriber cập nhật kỹ năng
        this.subscriberService.sendSubscribersEmailJobsForUser(updatedSub);

        return ResponseEntity.ok().body(updatedSub);
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }
}
