package iuh.fit.elearning.payment.controller;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.payment.service.PaymentFlowService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * UC010: các endpoint công khai của luồng thanh toán (trang MOCK, return / IPN VNPay).
 * Không yêu cầu đăng nhập; tính xác thực dựa trên chữ ký của cổng (VNPay) hoặc trạng thái đơn (MOCK).
 */
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private static final MediaType HTML_UTF8 = MediaType.parseMediaType("text/html;charset=UTF-8");

    private final PaymentFlowService flowService;

    public PaymentController(PaymentFlowService flowService) {
        this.flowService = flowService;
    }

    @GetMapping(value = "/mock/{orderCode}", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> mockPage(@PathVariable String orderCode) {
        return ResponseEntity.ok().contentType(HTML_UTF8).body(flowService.mockCheckoutPage(orderCode));
    }

    @PostMapping(value = "/mock/{orderCode}/complete", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> mockComplete(@PathVariable String orderCode,
                                               @RequestParam(defaultValue = "SUCCESS") String result) {
        return ResponseEntity.ok().contentType(HTML_UTF8).body(flowService.completeMock(orderCode, result));
    }

    @GetMapping(value = "/vnpay/return", produces = "text/html;charset=UTF-8")
    public ResponseEntity<String> vnpayReturn(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok().contentType(HTML_UTF8).body(flowService.vnpayReturn(params));
    }

    @GetMapping("/vnpay/ipn")
    public Map<String, String> vnpayIpn(@RequestParam Map<String, String> params) {
        return flowService.vnpayIpn(params);
    }

    @PostMapping("/momo/ipn")
    public void momoIpn() {
        throw BusinessException.badRequest("Phương thức MOMO chưa được hỗ trợ");
    }
}
