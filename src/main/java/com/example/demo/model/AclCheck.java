package com.example.demo.model;

import lombok.Data;

@Data
public class AclCheck {
    public Boolean check1;
    public Boolean check2;
    public Boolean check3;
    public Boolean check4;

    public Long checkValue1;
    public Long checkValue2;
    public Long checkValue3;
    public Long checkValue4;

    public AclCheck(Boolean check1, Boolean check2, Boolean check3, Boolean check4, Long checkValue1, Long checkValue2,
            Long checkValue3, Long checkValue4) {
        this.check1 = check1;
        this.check2 = check2;
        this.check3 = check3;
        this.check4 = check4;

        this.checkValue1 = checkValue1;
        this.checkValue2 = checkValue2;
        this.checkValue3 = checkValue3;
        this.checkValue4 = checkValue4;
    }
}
