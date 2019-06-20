package com.ebig.bms.repository.mis;

import com.ebig.bms.entity.DrugCodeUpSetting;
import com.ebig.bms.repository.Base.UpSettingRepository;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DrugCodeUpSettingRepository extends UpSettingRepository<DrugCodeUpSetting,Long> {

}
