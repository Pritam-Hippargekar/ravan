package com.ayush.ravan.elsticsearch.search.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Document(indexName = "#{@elasticSearchBarcodeSearchIndexName}", type = "#{@elasticSearchBarcodeSearchTypeName}")
@NoArgsConstructor
@Data
public class ESBarcodeDetailsType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    Long id;
    @JsonProperty("expiry_date")
    String expiryDate;
    @JsonProperty("pack_name")
    String packName;
    @JsonProperty("unit_name")
    String unitName;
    String form;
    @JsonProperty("sub_store_id")
    String subStoreId;
    @JsonProperty("store_id")
    String storeId;
    @JsonProperty("medicine_id")
    Long medicineId;
    @JsonProperty("curr_stock_qty")
    String currStockQty;
    @JsonProperty("bar_code")
    String barCode;
    @JsonProperty("medicine_name")
    String medicineName;
    @JsonProperty("MRP")
    String mrp;
    @JsonProperty("upper_pack")
    String upperPack;
}
