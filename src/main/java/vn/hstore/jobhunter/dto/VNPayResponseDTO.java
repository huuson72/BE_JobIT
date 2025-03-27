package vn.hstore.jobhunter.dto;

import lombok.Data;

@Data
public class VNPayResponseDTO {
    private String vnp_ResponseCode;
    private String vnp_TransactionNo;
    private String vnp_OrderInfo;
    private String vnp_PaymentDate;
    private String vnp_TxnRef;
    private String vnp_SecureHash;
} 