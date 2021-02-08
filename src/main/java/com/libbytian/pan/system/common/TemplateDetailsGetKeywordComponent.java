package com.libbytian.pan.system.common;

import com.libbytian.pan.system.model.SystemTemDetailsModel;
import com.libbytian.pan.system.model.SystemUserModel;
import com.libbytian.pan.system.service.ISystemTemDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TemplateDetailsGetKeywordComponent {


    private final ISystemTemDetailsService systemTemDetailsService;


    public SystemTemDetailsModel getUserKeywordDetail(SystemUserModel systemUserModel, String keyword) throws Exception {

        List<SystemTemDetailsModel> temDetailsModelList = systemTemDetailsService.getTemDetailsWithUser(systemUserModel);;
        SystemTemDetailsModel resultModel = null;
        for (SystemTemDetailsModel systemTemDetailsModel : temDetailsModelList ){

            if(systemTemDetailsModel.getKeyword().equals(keyword)){
                resultModel = systemTemDetailsModel;
                break;
            }
        }

        return resultModel;

    }

}
