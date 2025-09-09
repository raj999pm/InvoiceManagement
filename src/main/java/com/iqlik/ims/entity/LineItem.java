package com.iqlik.ims.entity;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class LineItem {


    private Long id;
    private String itemDescription;
    private int quantity;
    private BigDecimal unitPriceExclVat;
    private BigDecimal discountPerLine;
    private BigDecimal netLineTotal;
    private BigDecimal vatRate;
    private BigDecimal vatAmountPerLine;
}
