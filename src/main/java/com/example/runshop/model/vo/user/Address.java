package com.example.runshop.model.vo.user;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Address {
    private String street;         // 도로명 주소
    private String detailedAddress; // 상세 주소 (아파트 호수 등)
    private String city;           // 도시 (시/군/구)
    private String region;         // 광역시/도 (서울, 경기도 등)
    private String zipCode;        // 우편번호

    @Override
    public int hashCode() {
        return Objects.hash(street, detailedAddress, city, region, zipCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(detailedAddress, address.detailedAddress) &&
                Objects.equals(city, address.city) &&
                Objects.equals(region, address.region) &&
                Objects.equals(zipCode, address.zipCode);
    }

    @Override
    public String toString() {
        return street + ", " + detailedAddress + ", " + city + ", " + region + ", " + zipCode;
    }
}
